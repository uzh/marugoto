package ch.uzh.marugoto.backend.controller;

import javax.naming.AuthenticationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.uzh.marugoto.backend.security.AuthenticationFacade;
import ch.uzh.marugoto.backend.security.IAuthenticationFacade;
import ch.uzh.marugoto.core.data.entity.User;

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
	
	protected final Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private AuthenticationFacade authenticationFacade;
    

    protected User getAuthenticatedUser() throws AuthenticationException {
    	return authenticationFacade.getAuthenticatedUser();
    }
}
