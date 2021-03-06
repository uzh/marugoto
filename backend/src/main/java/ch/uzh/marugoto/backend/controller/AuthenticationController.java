package ch.uzh.marugoto.backend.controller;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.backend.resource.AuthToken;
import ch.uzh.marugoto.backend.resource.AuthUser;
import ch.uzh.marugoto.backend.security.JwtTokenProvider;
import ch.uzh.marugoto.core.data.entity.application.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AuthenticationController extends BaseController {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@ApiOperation(value = "Generates an access token regarding the login credentials. Add user to classroom if invitation link is provided.")
	@RequestMapping(value = "auth/generate-token", method = RequestMethod.POST)
	public AuthToken authenticate(@ApiParam(value = "mail and password") @RequestBody AuthUser loginUser)
			throws org.springframework.security.core.AuthenticationException {

		var authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getMail(), loginUser.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		var token = jwtTokenProvider.generateToken(authentication);
		var refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
		log.info("Token generated: " + jwtTokenProvider.getUserFromToken(token).getUsername() + " ["
				+ LocalDateTime.now() + "]");

		return new AuthToken(token, refreshToken);
	}

	@ApiOperation(value = "Returns authenticated user", authorizations = { @Authorization(value = "apiKey") })
	@RequestMapping(value = "auth/validate", method = RequestMethod.GET)
	public User validate() throws javax.naming.AuthenticationException {
		return getAuthenticatedUser();
	}

	@ApiOperation(value = "Get new tokens if the token is access token is expired", authorizations = {
			@Authorization(value = "apiKey") })
	@RequestMapping(value = "auth/refresh-token", method = RequestMethod.GET)
	public AuthToken refreshToken() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var token = jwtTokenProvider.generateToken(authentication);
		var refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

		log.info("Token refreshed: " + jwtTokenProvider.getUserFromToken(token).getUsername() + " ["
				+ LocalDateTime.now() + "]");

		return new AuthToken(token, refreshToken);
	}
}
