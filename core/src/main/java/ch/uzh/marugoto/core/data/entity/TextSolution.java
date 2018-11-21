package ch.uzh.marugoto.core.data.entity;

/**
 * Text Solution entity
 *
 */
public class TextSolution {
	
	private String textToCompare;
	private TextSolutionMode mode;

	public TextSolution() {
		super();
	}

	public TextSolution(String textToCompare, TextSolutionMode mode) {
		super();
		this.textToCompare = textToCompare;
		this.mode = mode;
	}

	public String getTextToCompare() {
		return textToCompare;
	}

	public void setTextToCompare(String textToCompare) {
		this.textToCompare = textToCompare;
	}

	public TextSolutionMode getMode() {
		return mode;
	}

	public void setMode(TextSolutionMode mode) {
		this.mode = mode;
	}
}
