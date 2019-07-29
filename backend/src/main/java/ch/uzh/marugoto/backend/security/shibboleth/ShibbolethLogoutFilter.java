package ch.uzh.marugoto.backend.security.shibboleth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

class ShibbolethLogoutFilter extends LogoutFilter {

        private List<LogoutHandler> _handlers;

        // configuration settings + default values
        private String principalUsernameAttribute;
        private String authenticationMethodAttribute;
        private String identityProviderAttribute;

        /** Ensure all configuration settings are set */
        @Override
        public void afterPropertiesSet() throws ServletException {
            super.afterPropertiesSet();

            Assert.notNull(principalUsernameAttribute, "principalUsernameAttribute cannot be null");
            Assert.notNull(authenticationMethodAttribute, "authenticationMethodAttribute cannot be null");
            Assert.notNull(identityProviderAttribute, "identityProviderAttribute cannot be null");
        }

        /** Constructor */
        public ShibbolethLogoutFilter() {
            super(new DummyLogoutSuccessHandler(), new DummyLogoutHandler());
        }

        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            if (requiresLogout(request, response)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if (logger.isDebugEnabled()) {
                    logger.debug("Logging out user '" + auth + "' silently");
                }

                for (LogoutHandler handler : _handlers) {
                    handler.logout(request, response, auth);
                }
            }

            chain.doFilter(request, response);
        }

        protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {

            boolean logout = false;

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return false;
            } else if ( ! auth.isAuthenticated() ) {
                return false;
            } else if ( ShibbolethAuthenticationToken.class.isAssignableFrom(auth.getClass())) {
                ShibbolethAuthenticationToken token = (ShibbolethAuthenticationToken) auth;

                // These are set by mod_shib22 in Apache and passed through mod_jk
                // to the servlet (Tomcat, Glassfish, etc..)
                // This means you MUST trust the assertions chain made by mod_jk, and in
                // turn Apache, and in turn mod_shib22, and in turn the Shibboleth SP (shibd)
                // This is often referred to as "pre-authentication"
                String eppn = request.getRemoteUser();
                if (eppn != null) { logger.debug("request.getRemoteUser() = " + eppn); }
                String authenticationType = request.getAuthType();

                // These are configurable attributes to load

                // set defauts
                String authenticationMethod = "";
                String identityProvider = "";

                // get attributes
                Object authenticationMethodObject = request.getAttribute(this.authenticationMethodAttribute);
                Object identityProviderObject = request.getAttribute(this.identityProviderAttribute);
                Object principalUsernameObject = request.getAttribute(this.principalUsernameAttribute);

                // if they are non-null, convert to string, and overwrite defaults
                if (authenticationMethodObject != null) {
                    authenticationMethod = authenticationMethodObject.toString();
                } else { logger.debug("could not read session property " + this.authenticationMethodAttribute); }
                if (identityProviderObject  != null) {
                    identityProvider = identityProviderObject.toString();
                } else { logger.debug("could not read session property " + this.identityProviderAttribute); }
                if (principalUsernameObject  != null) {
                    eppn = principalUsernameObject.toString();
                } else { logger.debug("could not read session property " + this.principalUsernameAttribute); }

                // INFO: authType is not configurable, as this plugin
                // is meant to be used with the Shibboleth Native SP that
                // integrates with Apache
                if (eppn == null) {
                    logout = true;
                    logger.debug("eppn is null, forcing logout");
                } else if ( ! token.getEppn().equals(eppn) ) {
                    logout = true;
                    logger.debug("eppn mismatch, expected '" + token.getEppn() + "', but have '" + eppn + "', forcing logout");
                } else if ( authenticationType == null ) {
                    logout = true;
                    logger.debug("authenticationType is null, forcing logout");
                } else if ( ! token.getAuthenticationType().equals(authenticationType) ) {
                    logout = true;
                    logger.debug("authenticationType mismatch, expected '" + token.getAuthenticationType() + "', but got '" + authenticationType + "', forcing logout");
                } else if ( authenticationMethod == null) {
                    logout = true;
                    logger.debug("authenticationMethod is null, forcing logout");
                } else if ( ! token.getAuthenticationMethod().equals(authenticationMethod) ) {
                    logout = true;
                    logger.debug("authenticationMethod mismatch, expected '" + token.getAuthenticationMethod() + "', but got '" + authenticationMethod + "', forcing logout");
                } else if ( identityProvider == null) {
                    logout = true;
                    logger.debug("identityProvider is null, forcing logout");
                } else if ( ! token.getIdentityProvider().equals(identityProvider) ) {
                    logout = true;
                    logger.debug("identityProvider mismatch, expected '" + token.getIdentityProvider() + "', but got '" + identityProvider + "', forcing logout");
                }
            }

            return logout;
        }

        public void setPrincipalUsernameAttribute(final String principalUsernameAttribute) {
            logger.debug("reading principalUsername from property: " + principalUsernameAttribute);
            this.principalUsernameAttribute = principalUsernameAttribute;
        }

        public void setAuthenticationMethodAttribute(final String authenticationMethodAttribute) {
            logger.debug("reading authenticationMethod from property: " + authenticationMethodAttribute);
            this.authenticationMethodAttribute = authenticationMethodAttribute;
        }

        public void setIdentityProviderAttribute(final String identityProviderAttribute) {
            logger.debug("reading identityProvider from property: " + identityProviderAttribute);
            this.identityProviderAttribute = identityProviderAttribute;
        }

        public void setHandlers(final List<LogoutHandler> handlers) {
            _handlers = handlers;
        }


        /**
         * Null logout handler that's used to provide a non-empty list of handlers to the base class.
         * The real handlers will be after construction.
         */
        private static class DummyLogoutSuccessHandler implements LogoutSuccessHandler {
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {
                // do nothing
            }
        }

        /**
         * Null logout handler that's used to provide a non-empty list of handlers to the base class.
         * The real handlers will be after construction.
         */
        private static class DummyLogoutHandler implements LogoutHandler {
            public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                // do nothing
            }
        }
}
