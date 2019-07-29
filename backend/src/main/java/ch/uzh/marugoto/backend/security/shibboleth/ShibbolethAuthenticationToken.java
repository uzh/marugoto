package ch.uzh.marugoto.backend.security.shibboleth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ShibbolethAuthenticationToken  extends AbstractAuthenticationToken {
    /**
     * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least one role, so
     * we give a user with no granted roles this one which gets past that restriction but
     * doesn't grant anything.
     */
    private static final List<GrantedAuthority> DEFAULT_AUTHORITIES = AuthorityUtils.createAuthorityList("ROLE_USER");

    // Extra token attributes for Shibboleth
    // These are all things that need to be pulled in from the
    // request object for processing by the ShibbolethUserDetailsService
    private Object details;
    private Object principal;
    private String username;
    private String eppn;
    private String authenticationType;
    private String authenticationMethod;
    private String identityProvider;
    private String authenticationInstant;
    private String remoteAddress;
    private Map<String, String> attributes;

    /** Constructor used by the authentication filter */
    public ShibbolethAuthenticationToken(String eppn, String username,
                                         String authenticationType, String authenticationMethod,
                                         String identityProvider, String authenticationInstant,
                                         String remoteAddress, Map<String, String> attributes) {

        super(DEFAULT_AUTHORITIES);

        this.details = null;
        this.principal = username;
        this.eppn = eppn;
        this.username = username;
        this.authenticationType = authenticationType;
        this.authenticationMethod = authenticationMethod;
        this.identityProvider = identityProvider;
        this.authenticationInstant = authenticationInstant;
        this.remoteAddress = remoteAddress;
        this.attributes = attributes;

        setAuthenticated(false);
    }

    /** Constructor used by the authentication provider */
    public ShibbolethAuthenticationToken(Collection<? extends GrantedAuthority> authorities,
                                         Object details, Object principal, String eppn, String username,
                                         String authenticationType, String authenticationMethod,
                                         String identityProvider, String authenticationInstant,
                                         String remoteAddress, Map<String, String> attributes) {

        super(authorities);

        this.details = details;
        this.principal = principal;
        this.eppn = eppn;
        this.username = username;
        this.authenticationType = authenticationType;
        this.authenticationMethod = authenticationMethod;
        this.identityProvider = identityProvider;
        this.authenticationInstant = authenticationInstant;
        this.remoteAddress = remoteAddress;
        this.attributes = attributes;

        setAuthenticated(true);
    }

    public String toString() {
        return super.toString() + ", eppn: '" + this.eppn + "'";
    }

    /** username just returns eppn */
    public String getUsername() {
        return username;
    }

    /** Getter for credentials */
    public Object getCredentials() {
        return null;
    }

    /** Getter for details */
    public Object getDetails() {
        return details;
    }

    /** Getter for principal */
    public Object getPrincipal() {
        return principal;
    }

    /** Getter for eppn */
    public String getEppn() {
        return eppn;
    }

    /** Getter for authenticationType */
    public String getAuthenticationType() {
        return authenticationType;
    }

    /** Getter for authenticationMethod */
    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    /** Getter for identityProvider */
    public String getIdentityProvider() {
        return identityProvider;
    }

    /** Getter for authenticationInstant */
    public String getAuthenticationInstant() {
        return authenticationInstant;
    }

    /** Getter for remoteAddress */
    public String getRemoteAddress() {
        return remoteAddress;
    }

    /** Getter for attributes */
    public Map<String, String> getAttributes() {
        return attributes;
    }
}
