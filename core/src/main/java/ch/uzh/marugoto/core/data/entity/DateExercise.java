package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.PersistenceConstructor;

public class DateExercise extends Exercise{

	private boolean isMandatory;
	private String placeholderText;
	private DateSolution solution;
	
	
	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public String getPlaceholderText() {
		return placeholderText;
	}

	public void setPlaceholderText(String placeholderText) {
		this.placeholderText = placeholderText;
	}
	
	public DateSolution getSolution() {
		return solution;
	}

	public void setSolution(DateSolution solution) {
		this.solution = solution;
	}

	@PersistenceConstructor
	public DateExercise(int numberOfColumns) {
		super(numberOfColumns);
	}

	public DateExercise(int numberOfColumns, boolean isMandatory, String placeholderText, DateSolution solution) {
		super(numberOfColumns);
		this.isMandatory = isMandatory;
		this.placeholderText = placeholderText;
		this.solution = solution;
	}

}
