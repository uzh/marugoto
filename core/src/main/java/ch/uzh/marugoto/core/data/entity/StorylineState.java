package ch.uzh.marugoto.core.data.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import com.arangodb.springframework.annotation.Document;
import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * storylineState contains the game-state of the user 
 *
 */
@Document
@JsonIgnoreProperties({"currentlyAt", "user"})
public class StorylineState {

	@Id
	private String id;
	private LocalDateTime startedAt;
	private LocalDateTime finishedAt;
	private LocalDateTime lastSavedAt;
	private double moneyBalance;
	private Duration virtualTimeBalance;
	@Ref(lazy = true)
	private PageState currentlyAt;
	@Ref
	private Storyline storyline;
	@Ref
	private User user;

	public StorylineState() {
		super();
		this.startedAt = LocalDateTime.now();
	}
	
	public StorylineState(Storyline storyline, User user) {
		this();
		this.storyline = storyline;
		this.user = user;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public LocalDateTime getStartedAt() {
		return startedAt;
	}
	
	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}
	
	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}
	
	public void setFinishedAt(LocalDateTime finishedAt) {
		this.finishedAt = finishedAt;
	}
	
	public LocalDateTime getLastSavedAt() {
		return lastSavedAt;
	}
	
	public void setLastSavedAt(LocalDateTime lastSavedAt) {
		this.lastSavedAt = lastSavedAt;
	}
	
	public double getMoneyBalance() {
		return moneyBalance;
	}
	
	public void setMoneyBalance(double moneyBalance) {
		this.moneyBalance = moneyBalance;
	}
	
	public Duration getVirtualTimeBalance() {
		return virtualTimeBalance;
	}
	
	public void setVirtualTimeBalance(Duration virtualTimeBalance) {
		this.virtualTimeBalance = virtualTimeBalance;
	}

	public Storyline getStoryline() {
		return storyline;
	}

	public void setStoryline(Storyline storyline) {
		this.storyline = storyline;
	}

	public PageState getCurrentlyAt() {
		return currentlyAt;
	}

	public void setCurrentlyAt(PageState currentlyAt) {
		this.currentlyAt = currentlyAt;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
