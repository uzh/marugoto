package ch.uzh.marugoto.core.data.entity.application;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndexed;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.PageState;

/**
 * Representing the user, who is playing the game.
 */
@Document
@JsonIgnoreProperties({"resetToken", "passwordHash", "currentPageState", "supervisor", "lastLoginAt"})
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
	private UserType type;
	private String resetToken;
	@Ref
	private PageState currentPageState;
	@Ref
	private GameState currentGameState;

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
		return firstName + " " + lastName;
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

	public GameState getCurrentGameState() {
		return currentGameState;
	}

	public void setCurrentGameState(GameState currentGameState) {
		this.currentGameState = currentGameState;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return mail.equals(user.mail) && type == user.type && Objects.equals(currentGameState, user.currentGameState);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mail, type);
	}
}