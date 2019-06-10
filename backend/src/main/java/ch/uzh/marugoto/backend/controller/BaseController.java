package ch.uzh.marugoto.backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.backend.security.AuthenticationFacade;
import ch.uzh.marugoto.backend.security.AuthorizationGate;
import ch.uzh.marugoto.core.data.entity.application.RequestAction;
import ch.uzh.marugoto.core.data.entity.application.User;

/**
 * Base API controller. Every controller implementation should inherit from this
 * one.
 * 
 * This base class provides a logger instance, see field {@code Log}.
 * 
 * Default URL route prefix is api/
 */
@RequestMapping("api")
public abstract class BaseController {
	
    @Autowired
    private AuthenticationFacade authenticationFacade;
    @Autowired
    private AuthorizationGate authorizationGate;
	protected final Logger log = LogManager.getLogger(this.getClass());

    protected User getAuthenticatedUser() throws AuthenticationException {
    	return authenticationFacade.getAuthenticatedUser();
    }

    protected void isUserAuthorized(RequestAction actionName, User user, Class modelGateClass, Object objectModel) {
        var authorised = authorizationGate.isUserAuthorized(actionName, user, modelGateClass, objectModel);
        if (!authorised) {
            throw new SecurityException();
        }
    }
}
