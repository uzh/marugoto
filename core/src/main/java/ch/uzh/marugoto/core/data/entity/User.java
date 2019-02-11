package ch.uzh.marugoto.core.data.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndexed;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.state.TopicState;

/**
 * Representing the user, who is playing the game.
 */
@Document
@JsonIgnoreProperties({"resetToken", "passwordHash", "currentPageState", "lastLoginAt"})
public class User {
	@Id
	private String id;
	private Salutation salutation;
	private String firstName;
	private String lastName;
	@HashIndexed(unique = true)
	private String mail;
	private String passwordHash;
	private LocalDateTime signedUpAt;
	private LocalDateTime lastLoginAt;
	private LocalDateTime activatedAt;
	private Boolean isSupervisor;
	private UserType type;
	private String resetToken;
	@Ref
	private PageState currentPageState;
	@Ref
	private TopicState currentTopicState;

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

	public String getName() {
		return salutation + " " + firstName + " " + lastName;
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

	public LocalDateTime getSignedUpAt() {
		return signedUpAt;
	}

	public void setSignedUpAt(LocalDateTime signedUpAt) {
		this.signedUpAt = signedUpAt;
	}

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(LocalDateTime lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public LocalDateTime getActivatedAt() {
		return activatedAt;
	}

	public void setActivatedAt(LocalDateTime activatedAt) {
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

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public PageState getCurrentPageState() {
		return currentPageState;
	}

	public void setCurrentPageState(PageState currentPageState) {
		this.currentPageState = currentPageState;
	}

	public TopicState getCurrentTopicState() {
		return currentTopicState;
	}

	public void setCurrentTopicState(TopicState currentTopicState) {
		this.currentTopicState = currentTopicState;
	}
}