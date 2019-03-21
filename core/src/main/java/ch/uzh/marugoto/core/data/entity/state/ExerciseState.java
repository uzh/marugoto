package ch.uzh.marugoto.core.data.entity.state;


import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import ch.uzh.marugoto.core.data.entity.topic.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.topic.Exercise;
import ch.uzh.marugoto.core.data.entity.topic.RadioButtonExercise;

/**
 * Exercise state - contains exercise component and user input text
 */
@Document
@JsonIgnoreProperties({"pageState", "exercise"})
public class ExerciseState {
	@Id
	private String id;
	private String inputState;
	@Ref
	private PageState pageState;
	@Ref
	private Exercise exercise;

	public ExerciseState() {
		super();
	}

	@PersistenceConstructor
	public ExerciseState(Exercise exercise) {
		super();
		this.exercise = exercise;
	}

	public ExerciseState(Exercise exercise, String inputText) {
		this(exercise);
		this.inputState = inputText;
	}

	public ExerciseState(Exercise exercise, String inputText, PageState pageState) {
		this(exercise);
		this.inputState = inputText;
		this.pageState = pageState;
	}

	public String getId() {
		return id;
	}

	public PageState getPageState() {
		return pageState;
	}

	public void setPageState(PageState pageState) {
		this.pageState = pageState;
	}

	public String getInputState() {
		return inputState;
	}

	public void setInputState(String inputText) {
		this.inputState = inputText;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}

	@JsonGetter
	public String getFormatedInput() {
		String value = null;

		if (exercise instanceof CheckboxExercise) {
			var stringBuilder = new StringBuilder();
			String[] inputList = inputState.split(",");
			for (String input : inputList) {
				if (inputList.length > 1) {
					stringBuilder.append("- ");
				}
				stringBuilder.append(((CheckboxExercise) exercise).getOptions().get(Integer.parseInt(input)).getText());
			}
			value = stringBuilder.toString();
		} else if (exercise instanceof RadioButtonExercise) {
			value = ((RadioButtonExercise) exercise).getOptions().get(Integer.parseInt(inputState)).getText();
		}

		return value;
	}
}
