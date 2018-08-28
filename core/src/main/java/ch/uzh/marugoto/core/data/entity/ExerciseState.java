package ch.uzh.marugoto.core.data.entity;


import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

/**
 * Exercise state - contains exercise component
 */

@Document
public class ExerciseState {
	@Id
	private String id;
	@Ref
	private Exercise exercise;

	public ExerciseState() {
		super();
	}

	public ExerciseState(Exercise exercise) {
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
}
