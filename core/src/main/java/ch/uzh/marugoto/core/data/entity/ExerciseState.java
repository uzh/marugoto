package ch.uzh.marugoto.core.data.entity;



import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Exercise state - contains exercise component and user input text
 */
@Document
@JsonIgnoreProperties({"pageState"})
public class ExerciseState {
	@Id
	private String id;
	private String inputText;
	@Ref
	private PageState pageState;
	@Ref
	private Exercise exercise;

	public ExerciseState() {
		super();
	}

	public ExerciseState(Exercise exercise) {
		this();
		this.exercise = exercise;
		this.inputText = "";
	}

	public PageState getPageState() {
		return pageState;
	}

	public void setPageState(PageState pageState) {
		this.pageState = pageState;
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
