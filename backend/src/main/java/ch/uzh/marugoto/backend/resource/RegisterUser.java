package ch.uzh.marugoto.backend.resource;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import ch.uzh.marugoto.core.data.entity.Salutation;

public class RegisterUser {
	
	private Salutation salutation;
	
	@NotEmpty(message = "Please provide your first name")
	private String firstName;
	@NotEmpty(message = "Please provide your last name")
	private String lastName;
	@NotEmpty(message = "Please provide an e-mail")
	@Email(message = "Please provide a valid e-mail")
	private String mail;
	private String password;
	
	public Salutation getSalutation() {
		return salutation;
	}
	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean validatePassword(String password) { 
		Pattern pattern = java.util.regex.Pattern.compile("((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,16})");
    	Matcher matcher = pattern.matcher(password);
    	return matcher.matches();
    }	
	

}
