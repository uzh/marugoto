package ch.uzh.marugoto.core.data.entity;

import java.util.List;

/**
 * Exercise with checkbox component
 */
public class CheckboxExercise extends Exercise {
	private List<Option> options; 

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public void addOption(Option option) {
		this.options.add(option);
	}
	
	public CheckboxExercise() {
		super();
	}
	
	public CheckboxExercise(int numberOfColumns,List<Option> options, Page page) {
		super(numberOfColumns, page);
		this.options = options;
	}
}
