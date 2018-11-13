package ch.uzh.marugoto.backend.resource;

import javax.validation.constraints.Email;

public class PasswordForget {

	@Email(message = "{badEmailFormat}")
	private String email;
	private String passwordResetUrl;
	
	public String getEmail() {
		return email;
	}
	public String getPasswordResetUrl() {
		return passwordResetUrl;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPasswordResetUrl(String passwordResetUrl) {
		this.passwordResetUrl = passwordResetUrl;
	}
}
