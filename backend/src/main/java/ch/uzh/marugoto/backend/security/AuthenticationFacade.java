package ch.uzh.marugoto.backend.security;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ch.uzh.marugoto.core.data.Messages;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.service.UserService;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

	@Autowired
	private UserService userService;
	@Autowired
	private Messages messages;
	
    @Override
    public Authentication getAuthentication() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (!(authentication instanceof AnonymousAuthenticationToken))
            return authentication;
        
        return null;
    }
    
    @Override
    public User getAuthenticatedUser() throws AuthenticationException {
    	Authentication auth = getAuthentication();

    	if (auth == null)
    		throw new AuthenticationException(messages.get("notAuthenticated"));
    	
    	User user = userService.getUserByMail(auth.getName());
    	if (user == null)
    		throw new AuthenticationException(messages.get("userNotExistForAuthentication"));
    	
    	return user;
    }
}