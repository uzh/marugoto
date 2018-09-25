package ch.uzh.marugoto.backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Qualifier("userService")
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		String header = req.getHeader(Constants.HEADER_STRING);
		String username = null;
		String authToken = null;

		if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
			authToken = header.replace(Constants.TOKEN_PREFIX, "");
			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
			} catch (IllegalArgumentException e) {
				logger.error("An error occured during token parsing.", e);
			} catch (ExpiredJwtException e) {
				logger.warn("Token has expired and not valid anymore.", e);
			} catch (SignatureException e) {
				logger.error("Authentication failed, credentials invalid.");
			} catch (MalformedJwtException e) {
				logger.error("Provided JWT token is malformed.", e);
			}
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (jwtTokenUtil.validateToken(authToken, userDetails)) {
				var authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
						Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
				
				logger.debug("Authenticated user " + username + ", setting security context");
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		chain.doFilter(req, res);
	}
}