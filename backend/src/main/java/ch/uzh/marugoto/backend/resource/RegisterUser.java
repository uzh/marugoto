package ch.uzh.marugoto.backend.resource;


import ch.uzh.marugoto.core.data.entity.Salutation;

public class RegisterUser {
	
	private Salutation salutation;
	private String firstName;
	private String lastName;
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
	public RegisterUser() {
		super();
	}

	public RegisterUser(Salutation salutation, String firstName, String lastName, String mail, String password) {
		super();
		this.salutation = salutation;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.password = password;
	}	
}
