package ch.uzh.marugoto.core.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.repository.UserRepository;

import org.springframework.security.core.userdetails.User;

/**
 * Service for handling user-related tasks like authentication, authorization and registration/sign-up.
 */
@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var applicationUser = userRepository.findByMail(username);
        if (applicationUser == null)
            throw new UsernameNotFoundException(username);
        
        return new User(applicationUser.getMail(), applicationUser.getPasswordHash(), Collections.emptyList());
    }
}