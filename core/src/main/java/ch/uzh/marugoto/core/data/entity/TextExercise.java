package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.PersistenceConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Exercise with the text input
 * 
 */
public class TextExercise extends Exercise {
	private int minLength;
	private int maxLength;
	private String placeholderText;
	private String defaultText;
	private List<TextSolution> textSolutions = new ArrayList<>();

	@PersistenceConstructor
	public TextExercise(int numberOfColumns, int minLength, int maxLength, String placeholderText) {
		super(numberOfColumns);
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.placeholderText = placeholderText;
	}

	public TextExercise(int numberOfColumns, int minLength, int maxLength, String placeholderText, Page page) {
		super(numberOfColumns, page);
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.placeholderText = placeholderText;
	}

	public TextExercise(int numberOfColumns, int minLength, int maxLength, String placeholderText, Page page, List<TextSolution> textSolutions) {
		this(numberOfColumns, minLength, maxLength, placeholderText, page);
		this.textSolutions = textSolutions;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getPlaceholderText() {
		return placeholderText;
	}

	public void setPlaceholderText(String placeholderText) {
		this.placeholderText = placeholderText;
	}

	public String getDefaultText() {
		return defaultText;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}

	public List<TextSolution> getTextSolutions() {
		return textSolutions;
	}

	public void setTextSolutions(List<TextSolution> textSolutions) {
		this.textSolutions = textSolutions;
	}

	public void addTextSolution(TextSolution solution) {
		this.textSolutions.add(solution);
	}
}
