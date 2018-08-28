package ch.uzh.marugoto.core.data.entity;


import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

/**
 * Exercise state - contains exercise component and user input text
 */

@Document
public class ExerciseState {
	@Id
	private String id;
	private String inputText;
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

	public String getInputText() {
		return inputText;
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}
}
