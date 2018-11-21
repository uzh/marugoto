package ch.uzh.marugoto.backend.controller;

import java.util.HashMap;
import java.util.List;

import javax.naming.AuthenticationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.marugoto.backend.security.AuthenticationFacade;
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
    
	protected String handleValidationErrors(List<FieldError>errors) throws JsonProcessingException, ParseException {
		HashMap<String, Object> errorMap = new HashMap<String, Object>();
		for (FieldError error : errors) {
			errorMap.put(error.getField(), error.getDefaultMessage());
		}
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(errorMap);
	}
}
