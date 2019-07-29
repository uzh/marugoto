package ch.uzh.marugoto.backend.security.shibboleth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class ShibbolethAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private String principalUsernameAttribute;
    private String usernameAttribute;
    private String authenticationMethodAttribute;
    private String identityProviderAttribute;
    private String authenticationInstantAttribute;
    private boolean usernameStripAtDomain;
    private Collection<String> extraAttributes;

    /** Ensure all configuration settings are set */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        Assert.notNull(principalUsernameAttribute, "principalUsernameAttribute cannot be null");
        Assert.notNull(usernameAttribute, "usernameAttribute cannot be null");
        Assert.notNull(authenticationMethodAttribute, "authenticationMethodAttribute cannot be null");
        Assert.notNull(identityProviderAttribute, "identityProviderAttribute cannot be null");
        Assert.notNull(authenticationInstantAttribute, "authenticationInstantAttribute cannot be null");
        Assert.notNull(extraAttributes, "extraAttributes cannot be null");
    }

    /** The default constructor */
    public ShibbolethAuthenticationFilter() {
        super("/j_spring_shibboleth_native_sp_security_check");
        this.setFilterProcessesUrl("/j_spring_shibboleth_native_sp_security_check");
    }

    public ShibbolethAuthenticationFilter(String filterProcessesUrl) {
        super(filterProcessesUrl);
        this.setFilterProcessesUrl(filterProcessesUrl);
    }

    /** Try logging in the user via Shibboleth Native SP */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        Authentication token = null;

        logger.debug("attemptAuthentication():: invocation");

        // These are set by mod_shib22 in Apache and passed through mod_jk
        // to the servlet (Tomcat, Glassfish, etc..)
        // This means you MUST trust the assertions chain made by mod_jk, and in
        // turn Apache, and in turn mod_shib22, and in turn the Shibboleth SP (shibd)
        // This is often referred to as "pre-authentication"
        String eppn = request.getRemoteUser();
        if (eppn != null) { logger.debug("request.getRemoteUser() = " + eppn); }
        String username = eppn;
        String remoteAddress = request.getRemoteAddr();
        String authType = request.getAuthType();

        // These are configurable attributes to load

        // set defauts
        String authenticationMethod = "";
        String identityProvider = "";
        String authenticationInstant = "";

        // get attributes
        Object authenticationMethodObject = request.getAttribute(this.authenticationMethodAttribute);
        Object identityProviderObject = request.getAttribute(this.identityProviderAttribute);
        Object principalUsernameObject = request.getAttribute(this.principalUsernameAttribute);
        Object authenticationInstantObject = request.getAttribute(this.authenticationInstantAttribute);
        Object usernameObject = request.getAttribute(this.usernameAttribute);

        // if they are non-null, convert to string, and overwrite defaults
        if (authenticationMethodObject != null) {
            authenticationMethod = authenticationMethodObject.toString();
        } else { logger.debug("could not read session property " + this.authenticationMethodAttribute); }
        if (identityProviderObject  != null) {
            identityProvider = identityProviderObject.toString();
        } else { logger.debug("could not read session property " + this.identityProviderAttribute); }
        if (principalUsernameObject  != null) {
            eppn = principalUsernameObject.toString();
        } else { logger.debug("could not read session property " + this.principalUsernameAttribute + " for eppn"); }

        if (authenticationInstantObject  != null) {
            authenticationInstant = authenticationInstantObject.toString();
        } else { logger.debug("could not read session property " + this.authenticationInstantAttribute); }
        if (usernameObject != null) {
            username = usernameObject.toString();
        } else { logger.debug("could not read session property " + this.usernameAttribute + " for username"); }

        // support stripping of the @domain.edu part of the username if the app doesn't want to use it.
        if (usernameStripAtDomain && username != null) {
            // look for an @
            int atPosition = username.indexOf('@');
            // If it's at least after the first character...
            if (atPosition > 1) {
                username = username.substring(0, atPosition);
            }
        }

        HashMap<String, String> attributes = new HashMap<String, String>();

        // load any extra attributes
        for (String key : this.extraAttributes) {
            Object valObject = request.getAttribute(key);
            if (valObject != null) {
                String val = valObject.toString();
                attributes.put(key, val);
            }
        }

        // INFO: authType is not configurable, as this plugin
        // is meant to be used with the Shibboleth Native SP that
        // integrates with Apache
        if (eppn == null) {
            logger.debug("eppn is null.  No valid shibboleth session found.");
        } else if ( eppn.length() <= 0 ) {
            logger.debug("eppn is empty.  No valid shibboleth session found.");
        } else if (username == null) {
            logger.debug("username is null.  No valid shibboleth session found.");
        } else if ( username.length() <= 0 ) {
            logger.debug("username is empty.  No valid shibboleth session found.");
        } else if ( authType == null ) {
            logger.debug("authType is null.   No valid shibboleth session found.");
        } else if ( ! authType.equals("shibboleth") ) {
            logger.debug("authType is not 'shibboleth'.  No valid shibboleth session found.");
        } else {
            // create the token
            // principal is set to eppn because the default string convert (toString)
            // for the AbstractAuthenticationProcessingFilter class is principal.toString()

            logger.debug("building a shibboleth token");

            ShibbolethAuthenticationToken shibbolethAuthenticationToken = new
                    ShibbolethAuthenticationToken(eppn, username, authType, authenticationMethod,
                    identityProvider, authenticationInstant, remoteAddress, attributes);

            logger.debug("calling authenticate()");
            token = this.getAuthenticationManager().authenticate(shibbolethAuthenticationToken);
        }

        return token;
    }

    public void setPrincipalUsernameAttribute(final String principalUsernameAttribute) {
        this.principalUsernameAttribute = principalUsernameAttribute;
    }

    public void setUsernameAttribute(final String usernameAttribute) {
        this.usernameAttribute = usernameAttribute;
    }

    public void setAuthenticationMethodAttribute(final String authenticationMethodAttribute) {
        this.authenticationMethodAttribute = authenticationMethodAttribute;
    }

    public void setIdentityProviderAttribute(final String identityProviderAttribute) {
        this.identityProviderAttribute = identityProviderAttribute;
    }

    public void setAuthenticationInstantAttribute(final String authenticationInstantAttribute) {
        this.authenticationInstantAttribute = authenticationInstantAttribute;
    }

    public void setUsernameStripAtDomain(final boolean usernameStripAtDomain) {
        this.usernameStripAtDomain = usernameStripAtDomain;
    }

    public void setExtraAttributes(final Collection<String> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

}