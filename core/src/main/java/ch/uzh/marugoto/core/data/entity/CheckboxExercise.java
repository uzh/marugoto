package ch.uzh.marugoto.core.data.entity;

import java.util.List;

import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Document;

/**
 * Exercise with checkbox component
 */
@Document
public class CheckboxExercise extends Exercise {

	private List<Option> minSelection;
	private List<Option> maxSelection;
	private List<Option> options;
	private CheckboxExerciseMode mode;

	public List<Option> getMinSelection() {
		return minSelection;
	}

	public void setMinSelection(List<Option> minSelection) {
		this.minSelection = minSelection;
	}

	public List<Option> getMaxSelection() {
		return maxSelection;
	}

	public void setMaxSelection(List<Option> maxSelection) {
		this.maxSelection = maxSelection;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public void addOption (Option option) {
		this.options.add(option);
	}

	public CheckboxExerciseMode getMode() {
		return mode;
	}

	public void setMode(CheckboxExerciseMode mode) {
		this.mode = mode;
	}

	public CheckboxExercise(int numberOfColumns) {
		super(numberOfColumns);
	}
	@PersistenceConstructor
	public CheckboxExercise(int numberOfColumns, List<Option> minSelection, List<Option> maxSelection,
			List<Option> options, CheckboxExerciseMode mode) {
		super(numberOfColumns);
		this.minSelection = minSelection;
		this.maxSelection = maxSelection;
		this.options = options;
		this.mode = mode;
	}
}
