package ch.uzh.marugoto.core.data.entity;



import com.arangodb.springframework.annotation.Ref;

/**
 * Exercise state - contains exercise component and user input text
 */
public class ExerciseState {
	private String inputText;
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
