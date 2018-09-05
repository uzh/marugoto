package ch.uzh.marugoto.core.data.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndexed;
import com.arangodb.springframework.annotation.Ref;

/**
 * Representing the user, who is playing the game.
 */
@Document
public class User {

	@Id
	private String id;
	private Salutation salutation;
	private String firstName;
	private String lastName;
	@HashIndexed(unique = true)
	private String mail;
	private String passwordHash;
	private Date signedUpAt;
	private Date lastLoginAt;
	private Date activatedAt;
	private Boolean isSupervisor;
	private UserType type;
	@Ref 
	private StorylineState currentlyPlaying;
	
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

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
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

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public StorylineState getCurrentlyPlaying() {
		return currentlyPlaying;
	}

	public void setCurrentlyPlaying(StorylineState currentlyPlaying) {
		this.currentlyPlaying = currentlyPlaying;
	}

	public User() {
		super();
	}

	public User(UserType type, Salutation salutation, String firstName, String lastName, String mail, String passwordHash) {
		super();
		this.type = type;
		this.salutation = salutation;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.passwordHash = passwordHash;
	}
}