package ch.uzh.marugoto.backend.resource;

import ch.uzh.marugoto.backend.security.Constants;

public class AuthToken {
	private final String prefix = Constants.TOKEN_PREFIX;
	private String token;
	private String refreshToken;
	
	public AuthToken() {
		super();
	}

	public AuthToken(String token) {
		this.token = prefix + " " + token;
	}

	public AuthToken(String token, String refreshToken) {
		this(token);
		this.refreshToken = prefix + " " + refreshToken;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = prefix + " " + token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = prefix + " " + refreshToken;
	}
}
