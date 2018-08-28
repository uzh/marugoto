package ch.uzh.marugoto.core.data.entity;

/**
 * Text Solution entity
 *
 */
public class TextSolution {

	private String textToCompare;
	private boolean useFuzzyComparison;
	private TextSolutionMode mode;

	public TextSolution() {
		super();
	}

	public TextSolution(String textToCompare) {
		this();
		this.textToCompare = textToCompare;
		this.mode = TextSolutionMode.contains;
		this.useFuzzyComparison = true;
	}
	
	public TextSolution(String textToCompare, TextSolutionMode mode, boolean useFuzzyComparison) {
		this();
		this.textToCompare = textToCompare;
		this.mode = mode;
		this.useFuzzyComparison = useFuzzyComparison;
	}

	public String getSolution() {
		return textToCompare;
	}
	public void setSolution(String textToCompare) {
		this.textToCompare = textToCompare;
	}
}
