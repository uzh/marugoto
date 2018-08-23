package ch.uzh.marugoto.core.data.entity;

/**
 * Text Solution entity
 *
 */
public class TextSolution {

	private String solution;

	public TextSolution() {
		super();
	}

	public TextSolution(String solution) {
		this();
		this.solution = solution;
	}

	public String getSolution() {
		return solution;
	}
	public void setSolution(String solution) {
		this.solution = solution;
	}
}
