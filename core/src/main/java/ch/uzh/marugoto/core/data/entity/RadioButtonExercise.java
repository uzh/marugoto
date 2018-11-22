package ch.uzh.marugoto.core.data.entity;

import java.util.List;

/**
 * Exercise with radio component
 */
public class RadioButtonExercise extends Exercise {
	
	private List<Option> options;
	
	public List<Option> getOptions() {
		return options;
	}
	
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public RadioButtonExercise () {
		super();
	}
	
	public RadioButtonExercise(int numberOfColumns, List<Option> options, Integer correctOption) {
		super(numberOfColumns);
		this.options = options;
	}

	public RadioButtonExercise(int numberOfColumns, List<Option> options, Integer correctOption, Page page) {
		super(numberOfColumns, page);
		this.options = options;
	}
}
