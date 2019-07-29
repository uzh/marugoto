package ch.uzh.marugoto.backend.security.shibboleth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;

@Component
public class ShibbolethAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    // Support for Shibboleth User Details Service
    private AuthenticationUserDetailsService<ShibbolethAuthenticationToken> authenticationUserDetailsService;

    @Qualifier("userService")
    @Autowired private UserDetailsService userDetailsService;

    public void afterPropertiesSet() throws Exception {
        boolean oneIsSet = (authenticationUserDetailsService != null || userDetailsService != null);
        Assert.isTrue(oneIsSet, "An authenticationUserDetailsService or userDetailsService must be set");
    }

    public ShibbolethAuthenticationProvider() {
        super();
    }

    /**
     This attempts to authenticate an {@link Authentication} using the native Shibboleth SP
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // exit if unsupported token is passed
        if (!supports(authentication.getClass())) {
            return null;
        }

        // cast token to a ShibbolethAuthenticationToken
        ShibbolethAuthenticationToken shibToken = (ShibbolethAuthenticationToken) authentication;

        if (shibToken.getAuthenticationType() == null) {
            throw new BadCredentialsException("authenticationType is null");
        } else if (shibToken.getAuthenticationType().length() == 0) {
            throw new BadCredentialsException("authenticationType is empty");
        } else if ( ! shibToken.getAuthenticationType().equals("shibboleth") ) {
            throw new BadCredentialsException("authenticationType, '" + shibToken.getAuthenticationType() + "' != 'shibboleth'");
        } else if (shibToken.getEppn() == null) {
            throw new BadCredentialsException("eppn is null");
        } else if (shibToken.getEppn().length() == 0) {
            throw new BadCredentialsException("eppn is empty");
        } else if (shibToken.getIdentityProvider() == null) {
            throw new BadCredentialsException("identityProvider is null");
        } else if (shibToken.getIdentityProvider().length() == 0) {
            throw new BadCredentialsException("identityProvider is empty");
        } else if (shibToken.getAuthenticationInstant() == null) {
            throw new BadCredentialsException("authenticationInstant is null");
        } else if (shibToken.getAuthenticationInstant().length() == 0) {
            throw new BadCredentialsException("authenticationInstant is empty");
        } else if (shibToken.getAuthenticationMethod() == null) {
            throw new BadCredentialsException("authenticationMethod is null");
        } else if (shibToken.getAuthenticationMethod().length() == 0) {
            throw new BadCredentialsException("authenticationMethod is empty");
        }

        //TODO: Insert user if it doesn't exists

        // set default principal and authorities
        Object principal;
        Collection<? extends GrantedAuthority> authorities = shibToken.getAuthorities();

        // load user details from the authentication
        UserDetails userDetails;
        if (this.authenticationUserDetailsService != null) {
            userDetails = this.authenticationUserDetailsService.loadUserDetails(shibToken);
        } else {
            userDetails = this.userDetailsService.loadUserByUsername(shibToken.getUsername());
        }

        if (userDetails != null) {
            principal = userDetails;
            authorities = userDetails.getAuthorities();
        } else {
            principal = shibToken.getEppn();
        }

        return new ShibbolethAuthenticationToken(authorities,
                shibToken.getDetails(), principal, shibToken.getEppn(), shibToken.getUsername(),
                shibToken.getAuthenticationType(), shibToken.getAuthenticationMethod(),
                shibToken.getIdentityProvider(), shibToken.getAuthenticationInstant(),
                shibToken.getRemoteAddress(), shibToken.getAttributes());

    }

    /** Returns true if the Authentication implementation passed is supported
     * by the {@code ShibbolethAuthenticationProvider#authenticate} method.
     */
    public boolean supports(Class<?> authentication) {
        return ShibbolethAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Used to load the authorities and user details for the Shibboleth service
     */
    public void setUserDetailsService(final UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Used to load the authorities and user details for the Shibboleth service
     */
    public void setAuthenticationUserDetailsService(final AuthenticationUserDetailsService<ShibbolethAuthenticationToken> authenticationUserDetailsService) {
        this.authenticationUserDetailsService = authenticationUserDetailsService;
    }
}