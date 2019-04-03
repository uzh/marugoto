package ch.uzh.marugoto.core.data.entity.topic;

/**
 * Text Solution entity
 *
 */
public class TextSolution {
	
	private String textToCompare;
	private Integer minLength;
	private TextSolutionMode mode;

	public TextSolution() {
		super();
	}
	
	public TextSolution(int minLength, TextSolutionMode mode) {
		this();
		this.minLength = minLength;
		this.mode = mode;
	}

	public TextSolution(String textToCompare, TextSolutionMode mode) {
		this();
		this.textToCompare = textToCompare;
		this.mode = mode;
	}

	public String getTextToCompare() {
		return textToCompare;
	}

	public void setTextToCompare(String textToCompare) {
		this.textToCompare = textToCompare;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public TextSolutionMode getMode() {
		return mode;
	}

	public void setMode(TextSolutionMode mode) {
		this.mode = mode;
	}
}
