package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import javax.naming.AuthenticationException;

import ch.uzh.marugoto.backend.resource.AuthToken;
import ch.uzh.marugoto.backend.resource.AuthUser;
import ch.uzh.marugoto.backend.security.Constants;
import ch.uzh.marugoto.backend.security.JwtTokenUtil;
import ch.uzh.marugoto.core.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AuthenticationController extends BaseController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "auth/generate-token", method = RequestMethod.POST)
	public AuthToken register(@RequestBody AuthUser loginUser) throws org.springframework.security.core.AuthenticationException, AuthenticationException {
		var authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getMail(), loginUser.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		var user = userService.loadUserByUsername(loginUser.getMail());
		var token = jwtTokenUtil.generateToken(user);
		userService.updateLastLoginAt(authenticationFacade.getAuthenticatedUser());
		
		return new AuthToken(Constants.TOKEN_PREFIX + " " + token);
	}

	@RequestMapping(value = "auth/validate", method = RequestMethod.GET)
	public Object validate() throws javax.naming.AuthenticationException {
		var user = authenticationFacade.getAuthenticatedUser();

		var res = new HashMap<String, Object>();
		res.put("mail", user.getMail());
		res.put("firstName", user.getFirstName());
		res.put("lastName", user.getLastName());
		
		return res;
	}
}
