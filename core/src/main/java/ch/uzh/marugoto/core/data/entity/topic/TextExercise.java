package ch.uzh.marugoto.core.data.entity.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Exercise with the text input
 * 
 */
@JsonIgnoreProperties({"textSolutions"})
public class TextExercise extends Exercise {

	private Integer maxLength;
	private String placeholderText;
	private String defaultText;
	private List<TextSolution> textSolutions = new ArrayList<>();

	public TextExercise() {
		super();
	}

	public TextExercise(int numberOfColumns, int maxLength, String placeholderText, Page page) {
		super(numberOfColumns, page);
		this.maxLength = maxLength;
		this.placeholderText = placeholderText;
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
