package ch.uzh.marugoto.backend.resource;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class PasswordForget {

	@Email
	@NotEmpty(message = "{mail.notEmpty}")
	private String email;
	@NotEmpty(message = "{passwordResetUrl.notEmpty}")
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
	public PasswordForget() {
		super();
	}

	public PasswordForget(String email,String passwordResetUrl) {
		super();
		this.email = email;
		this.passwordResetUrl = passwordResetUrl;
	}
}
