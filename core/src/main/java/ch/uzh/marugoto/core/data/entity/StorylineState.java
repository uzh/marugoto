package ch.uzh.marugoto.core.data.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Ref;

public class StorylineState {

	@Id
	private String id;
	private LocalDateTime startedAt;
	private LocalDateTime finishedAt;
	private LocalDateTime lastSavedAt;
	private String moneyBalance;
	private Duration virtualTimeBalance;
	@Ref
	private Storyline storyline;
	@Ref
	private PageState currently_at;
	@Ref
	private User user;

	public StorylineState() {
		super();
	}
	
	public StorylineState(LocalDateTime startedAt, User user) {
		super();
		this.startedAt = startedAt;
		this.user = user;
	}
	
	public StorylineState(LocalDateTime startedAt, Storyline storyline, User user) {
		super();
		this.startedAt = startedAt;
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
	
	public String getMoneyBalance() {
		return moneyBalance;
	}
	
	public void setMoneyBalance(String moneyBalance) {
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

	public PageState getCurrently_at() {
		return currently_at;
	}

	public void setCurrently_at(PageState currently_at) {
		this.currently_at = currently_at;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
