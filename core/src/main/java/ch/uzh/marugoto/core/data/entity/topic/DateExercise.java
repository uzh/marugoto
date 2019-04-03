package ch.uzh.marugoto.core.data.entity.topic;

/**
 * Date exercise
 */
public class DateExercise extends Exercise{
	private boolean mandatory;
	private String placeholderText;
	private DateSolution solution;
	
	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.mandatory = isMandatory;
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
	
	public DateExercise () {
		super();
	}

	public DateExercise(int numberOfColumns, boolean isMandatory, String placeholderText, DateSolution solution, Page page) {
		super(numberOfColumns, page);
		this.mandatory = isMandatory;
		this.placeholderText = placeholderText;
		this.solution = solution;
	}

}
