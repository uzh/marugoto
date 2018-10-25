package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.PersistenceConstructor;

import java.util.List;

/**
 * Exercise with radio component
 */
public class RadioButtonExercise extends Exercise {
	private List<Option> options;
	private Integer correctOption;
	
	public List<Option> getOptions() {
		return options;
	}
	
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public Integer getCorrectOption() {
		return correctOption;
	}
	
	public void setCorrectOption(Integer correctOption) {
		this.correctOption = correctOption;
	}

	@PersistenceConstructor
	public RadioButtonExercise(int numberOfColumns, List<Option> options, Integer correctOption) {
		super(numberOfColumns);
		this.options = options;
		this.correctOption = correctOption;
	}

	public RadioButtonExercise(int numberOfColumns, List<Option> options, Integer correctOption, Page page) {
		super(numberOfColumns, page);
		this.options = options;
		this.correctOption = correctOption;
	}
}
