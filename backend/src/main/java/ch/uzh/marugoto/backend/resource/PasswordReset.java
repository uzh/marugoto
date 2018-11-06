package ch.uzh.marugoto.backend.resource;

import javax.validation.constraints.Email;

import ch.uzh.marugoto.backend.validation.Password;

public class PasswordReset {

	@Email
	private String userEmail;
	private String token;
	@Password(message = "{passwordValidation}")
	private String newPassword;
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public PasswordReset () {
		super();
	}	
	
	public PasswordReset(@Email String userEmail, String token, String newPassword) {
		super();
		this.userEmail = userEmail;
		this.token = token;
		this.newPassword = newPassword;
	}

}