package ch.uzh.marugoto.core.data.entity.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Exercise with checkbox component
 */
@JsonIgnoreProperties({"solutionMode"})
public class CheckboxExercise extends Exercise {
	private List<ExerciseOption> options;
	private CheckboxSolutionMode solutionMode = CheckboxSolutionMode.correct;
	private int minimumSelected;

	public CheckboxExercise() {
		super();
	}

	public CheckboxExercise(int numberOfColumns, List<ExerciseOption> options, Page page) {
		super(numberOfColumns, page);
		this.options = options;
	}

	public List<ExerciseOption> getOptions() {
		return options;
	}

	public void setOptions(List<ExerciseOption> options) {
		this.options = options;
	}

	public void addOption(ExerciseOption option) {
		this.options.add(option);
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
}
