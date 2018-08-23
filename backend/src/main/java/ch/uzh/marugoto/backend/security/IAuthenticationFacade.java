package ch.uzh.marugoto.backend.security;

import javax.naming.AuthenticationException;

import org.springframework.security.core.Authentication;

import ch.uzh.marugoto.core.data.entity.User;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    
    User getAuthenticatedUser() throws AuthenticationException;
}
