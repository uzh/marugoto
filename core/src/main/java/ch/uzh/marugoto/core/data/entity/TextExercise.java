package ch.uzh.marugoto.core.data.entity;

import java.util.ArrayList;
import java.util.List;

import com.arangodb.springframework.annotation.Document;

/**
 * 
 * Exercise with the text input
 * 
 */

@Document
public class TextExercise extends Exercise {
	private int minLength;
	private int maxLength;
	private String placeholderText;
	private String defaultText;
	private List<TextSolution> textSolutions;

	
	public TextExercise() {
		super();
	}

	public TextExercise(int numberOfColumns, int minLength, int maxLength, String defaultText) {
		super(numberOfColumns);
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.defaultText = defaultText;
		this.textSolutions = new ArrayList<>();
	}

	public TextExercise(int numberOfColumns, int minLength, int maxLength, String defaultText, String placeholderText) {
		this(numberOfColumns, minLength, maxLength, defaultText);
		this.placeholderText = placeholderText;
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
