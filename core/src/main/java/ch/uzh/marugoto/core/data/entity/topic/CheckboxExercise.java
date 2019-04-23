package ch.uzh.marugoto.core.data.entity.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Checkbox Exercise component
 */
@JsonIgnoreProperties({"solutionMode", "minimumSelected"})
public class CheckboxExercise extends Exercise {

	private List<ExerciseOption> options;
	private CheckboxSolutionMode solutionMode = CheckboxSolutionMode.correct;
	private Integer minimumSelected;

	public CheckboxExercise() {
		super();
	}

	public CheckboxExercise(int numberOfColumns, Page page) {
		super(numberOfColumns, page);
	}

	public List<ExerciseOption> getOptions() {
		return options;
	}

	public void setOptions(List<ExerciseOption> options) {
		this.options = options;
	}

	public int getMinimumSelected() {
		return minimumSelected;
	}

	public void setMinimumSelected(int minimumSelected) {
		this.minimumSelected = minimumSelected;
	}

	public CheckboxSolutionMode getSolutionMode() {
		return solutionMode;
	}

	public void setSolutionMode(CheckboxSolutionMode solutionMode) {
		this.solutionMode = solutionMode;
	}
	
	public int getCorrectOptionsSize(CheckboxExercise checkboxExercise) {
		
		var counter = 0;
		for (ExerciseOption o : checkboxExercise.getOptions()) {
			if (o.isCorrect()) {
				counter++;
			}
		}
		return counter;
	}
	
	
}
