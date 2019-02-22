package ch.uzh.marugoto.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.backend.security.AuthenticationFacade;
import ch.uzh.marugoto.core.data.Messages;
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
	protected final Logger log = LogManager.getLogger(this.getClass());

    protected User getAuthenticatedUser() throws AuthenticationException {
    	return authenticationFacade.getAuthenticatedUser();
    }

    protected void isSupervisorAuthenticated() throws AuthenticationException {
    	if (!getAuthenticatedUser().isSupervisor()) {
    		throw new AuthenticationException();
		}
	}
}
