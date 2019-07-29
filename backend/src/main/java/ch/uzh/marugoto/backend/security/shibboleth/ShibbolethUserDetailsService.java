package ch.uzh.marugoto.backend.security.shibboleth;

import ch.uzh.marugoto.backend.resource.ShibbolethUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

public class ShibbolethUserDetailsService implements UserDetailsService, AuthenticationUserDetailsService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * This is to support the {@code RememberMeService}
     */
    private final Map<String, ShibbolethUser> registeredUsers = new HashMap<>();

    /**
     * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least one role, so
     * we give a user with no granted roles this one which gets past that restriction but
     * doesn't grant anything.
     */
    private static final String DEFAULT_ROLES_ATTRIBUTE = null;
    private static final String DEFAULT_ROLES_SEPARATOR = "W,";
    private static final String DEFAULT_ROLES_PREFIX = "SHIB_";
    private static final String DEFAULT_EMAIL_ATTRIBUTE = "email";
    private static final String DEFAULT_FULLNAME_ATTRIBUTE = "commonName";

    /** This is the exposed attribute that contains the user's roles */
    String rolesAttribute = DEFAULT_ROLES_ATTRIBUTE;
    /** This is the delimiter for the roles attribute value */
    String rolesSeparator = DEFAULT_ROLES_SEPARATOR;
    /** This is the prefix to apply to all the roles loaded from the exposed roles attribute */
    String rolesPrefix = DEFAULT_ROLES_PREFIX;
    /** This is the optional attribute that contains the user's full name */
    String emailAttribute = DEFAULT_EMAIL_ATTRIBUTE;
    /** This is the optional attribute that contains the user's email address */
    String fullNameAttribute = DEFAULT_FULLNAME_ATTRIBUTE;

    /**
     * This is to support the {@code RememberMeService}
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Look up the user via RememberMeService
        UserDetails user = registeredUsers.get(username);

        // If the user isn't found, throw an exception
        if (user == null) { throw new UsernameNotFoundException(username); }

        // else return the remembered UserDetails
        return user;
    }

    /**
     * This loads the user details from the shibboleth attributes passed in the
     * {@code ShibbolethAuthenticationToken}
     */
    public UserDetails loadUserDetails(Authentication authentication) {

        ShibbolethAuthenticationToken shibAuthToken;

        // Try to convert the authentication to a ShibbolethAuthenticationToken
        try {
            shibAuthToken = (ShibbolethAuthenticationToken) authentication;
        } catch (ClassCastException ex) {
            logger.error(ex);
            throw new BadCredentialsException("you must provide a ShibbolethAuthenticationToken");
        }
        // Exit if the conversion was unsuccessful
        if (shibAuthToken == null) { return null; }

        // set default values
        String fullName = null;
        String username = shibAuthToken.getUsername();
        if (shibAuthToken.getAttributes().containsKey(DEFAULT_FULLNAME_ATTRIBUTE)) {
            fullName = shibAuthToken.getAttributes().get(DEFAULT_FULLNAME_ATTRIBUTE);
        }
        String email = null;
        if (shibAuthToken.getAttributes().containsKey(DEFAULT_EMAIL_ATTRIBUTE)) {
            email = shibAuthToken.getAttributes().get(DEFAULT_EMAIL_ATTRIBUTE);
        }
        String eppn = shibAuthToken.getEppn();
        Map<String, String> attributes = shibAuthToken.getAttributes();

        Collection<SimpleGrantedAuthority> newAuthorities = new ArrayList<>();

        // Load Shibboleth roles if enabled
        if (rolesAttribute != null && rolesSeparator != null && rolesPrefix != null && shibAuthToken.getAttributes().containsKey(rolesAttribute)) {
            String rolesString =  shibAuthToken.getAttributes().get(rolesAttribute);
            if (rolesString != null) {
                Collection<String> rolesCollection = new ArrayList<>(Arrays.asList(rolesString.split(rolesSeparator)));
                rolesCollection.forEach(it -> {
                    newAuthorities.add(new SimpleGrantedAuthority("ROLE_" + rolesPrefix + it.toUpperCase()));
                });
            }
        }

        if (fullNameAttribute != null && shibAuthToken.getAttributes().containsKey(fullNameAttribute)) {
            fullName = shibAuthToken.getAttributes().get(fullNameAttribute);
        }

        if (emailAttribute != null && shibAuthToken.getAttributes().containsKey(emailAttribute)) {
            email = shibAuthToken.getAttributes().get(emailAttribute);
        }

        // return new ShibbolethUser (principal)
        return new ShibbolethUser(username, email, fullName, newAuthorities, eppn, attributes);

    }
}