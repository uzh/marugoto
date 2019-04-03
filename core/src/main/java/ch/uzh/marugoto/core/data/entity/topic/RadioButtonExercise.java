package ch.uzh.marugoto.core.data.entity.topic;

import java.util.List;

/**
 * Exercise with radio component
 */
public class RadioButtonExercise extends Exercise {
	
	private List<ExerciseOption> options;

	public RadioButtonExercise () {
		super();
	}

	public RadioButtonExercise(int numberOfColumns, List<ExerciseOption> options, Page page) {
		super(numberOfColumns, page);
		this.options = options;
	}

	public List<ExerciseOption> getOptions() {
		return options;
	}

	public void setOptions(List<ExerciseOption> options) {
		this.options = options;
	}
}
