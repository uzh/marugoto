package ch.uzh.marugoto.backend.data.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndex;

@Document("user")
@HashIndex(fields = { "mail" }, unique = true)
public class User {
	@Id
	private String id;
	private Salutation salutation;
	private String firstName;
	private String lastName;
	private String mail;
	private Date signedUpAt;
	private Date lastLoginAt;
	private Date activatedAt;
	private Boolean isSupervisor;


	public String getId() {
		return id;
	}

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


	public Date getSignedUpAt() {
		return signedUpAt;
	}


	public void setSignedUpAt(Date signedUpAt) {
		this.signedUpAt = signedUpAt;
	}


	public Date getLastLoginAt() {
		return lastLoginAt;
	}


	public void setLastLoginAt(Date lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}


	public Date getActivatedAt() {
		return activatedAt;
	}


	public void setActivatedAt(Date activatedAt) {
		this.activatedAt = activatedAt;
	}


	public Boolean getIsSupervisor() {
		return isSupervisor;
	}


	public void setIsSupervisor(Boolean isSupervisor) {
		this.isSupervisor = isSupervisor;
	}


	public User(final Salutation salutation, final String firstName, final String lastName, final String mail) {
		super();
		this.salutation = salutation;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
	}
}