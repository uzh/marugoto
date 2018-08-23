package ch.uzh.marugoto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ch.uzh.marugoto.backend.resource.AuthToken;
import ch.uzh.marugoto.backend.resource.AuthUser;
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
	public ResponseEntity<?> register(@RequestBody AuthUser loginUser) throws AuthenticationException {
		var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		var user = userService.loadUserByUsername(loginUser.getUsername());
		var token = jwtTokenUtil.generateToken(user);

		return ResponseEntity.ok(new AuthToken(token));
	}
}
