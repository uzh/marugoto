package ch.uzh.marugoto.backend.security;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ch.uzh.marugoto.core.data.Messages;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtTokenProvider implements Serializable {
	private static final long serialVersionUID = -788827551303691289L;
	private final Logger logger = LogManager.getLogger(this.getClass());

	@Qualifier("userService")
	@Autowired private UserDetailsService userDetailsService;

	@Autowired
	private Messages messages;

	/**
	 * Resolves token from the request
	 *
	 * @param req http request
	 * @return
	 */
	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader(Constants.HEADER_STRING);
		String authToken = null;

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.TOKEN_PREFIX)) {
			authToken = bearerToken.replace(Constants.TOKEN_PREFIX, "");
		}

		return authToken;
	}

	/**
	 * Returns user from token
	 *
	 * @param authToken
	 * @return
	 */
	public UserDetails getUserFromToken(String authToken) {
		var username = getClaimFromToken(authToken, Claims::getSubject);
		return userDetailsService.loadUserByUsername(username);
	}

	/**
	 * Checks if token has expired
	 *
	 * @param token
	 * @return
	 */
	public Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	/**
	 * Generates token
	 *
	 * @param authentication
	 * @return token
	 */
	public String generateToken(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
		claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + Constants.ACCESS_TOKEN_VALIDITY_MS))
				.signWith(SignatureAlgorithm.HS256, Constants.SIGNING_KEY)
				.compact();
	}

	/**
	 * Generates refresh token used to initialize new token
	 *
	 * @param authentication
	 * @return token
	 */
	public String generateRefreshToken(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
		claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setId(UUID.randomUUID().toString())
				.setExpiration(new Date(System.currentTimeMillis() + Constants.REFRESH_TOKEN_VALIDITY_MS))
				.signWith(SignatureAlgorithm.HS256, Constants.SIGNING_KEY)
				.compact();
	}

	/**
	 * Validates token
	 *
	 * @param token
	 * @return
	 */
	public Boolean validateToken(String token) {
		try {
			getClaimFromToken(token, Claims::getSubject);
			return true;
		} catch (IllegalArgumentException e) {
			logger.error(messages.get("tokenParsingError"));
		} catch (ExpiredJwtException e) {
			logger.error(messages.get("tokenExpired"));
		} catch (SignatureException e) {
			logger.error(messages.get("badCredentials"));
		} catch (MalformedJwtException e) {
			logger.error(messages.get("tokenMalformed"));
		}

		return false;
	}

	private Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(Constants.SIGNING_KEY).parseClaimsJws(token).getBody();
	}
}
