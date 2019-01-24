package ch.uzh.marugoto.core.data.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * storylineState contains the game-state of the user 
 *
 */
@Document
public class StorylineState {
	@Id
	@JsonIgnore
	private String id;
	private LocalDateTime startedAt;
	private LocalDateTime finishedAt;
	private LocalDateTime lastSavedAt;
	private Money moneyBalance = new Money();
	private Duration virtualTimeBalance = Duration.ZERO;
	@Ref
	private Storyline storyline;

	public StorylineState() {
		super();
	}

	@PersistenceConstructor
	public StorylineState(Storyline storyline) {
		super();
		this.storyline = storyline;
		this.startedAt = LocalDateTime.now();
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
		return moneyBalance.getAmount();
	}
	
	public void setMoneyBalance(double moneyBalance) {
		this.moneyBalance.setAmount(moneyBalance);
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

	public boolean equals(Object o) {
		boolean equals = false;

		if (o instanceof StorylineState) {
			var storylineState = (StorylineState) o;
			equals = id.equals(storylineState.id);
		}

		return equals;
	}
}
