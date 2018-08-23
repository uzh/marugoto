
package ch.uzh.marugoto.backend.data.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

/**
 * Exercise state - should have information for the exercise
 */

@Document
public class ExerciseState {
	@Id
	private String id;
	private Exercise exercise;
	private LocalDateTime startedAt;
	private LocalDateTime finishedAt;
	
	public ExerciseState () {
		super();
		this.startedAt = LocalDateTime.now();
	}
	
	public ExerciseState (Exercise exercise) {
		this();
		this.exercise = exercise;
	}

	public String getId() {
		return id;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
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
}
