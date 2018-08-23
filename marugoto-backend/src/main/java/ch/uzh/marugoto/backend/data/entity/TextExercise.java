
package ch.uzh.marugoto.backend.data.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;

/**
 * 
 * Exercise with the text input
 * 
 */

@Document
public class TextExercise extends Exercise {

	private static final int DEFAULT_ROW_HEIGHT = 20;

	@Id
	private String id;
	private int minLength;
	private int maxLength;
	private String title;
	private String placeholderText;
	private String inputText;
	private int rowHeight;
	private List<TextSolution> solutions;

	
	public TextExercise() {
		super();
	}

	/**
	 * 
	 * Text exercise component with default row height
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param minLength
	 * @param maxLength
	 * @param placeholderText
	 * @param inputText
	 */
	public TextExercise(int x, int y, int width, int height, int minLength, int maxLength, String title, String placeholderText) {
		super(x, y, width, height);
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.title = title;
		this.placeholderText = placeholderText;
		this.rowHeight = TextExercise.DEFAULT_ROW_HEIGHT;
		this.solutions = new ArrayList<TextSolution>();
	}
	
	/**
	 * 
	 * Text exercise component with custom row height
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param minLength
	 * @param maxLength
	 * @param placeholderText
	 * @param inputText
	 * @param rowHeight
	 */
	public TextExercise(int x, int y, int width, int height, int minLength, int maxLength, String title, String placeholderText, int rowHeight) {
		this(x, y, width, height, minLength, maxLength, title, placeholderText);
		this.rowHeight = rowHeight;
	}
	
	public String getId() {
		return id;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPlaceholderText() {
		return placeholderText;
	}


	public void setPlaceholderText(String placeholderText) {
		this.placeholderText = placeholderText;
	}


	public String getDefaultText() {
		return inputText;
	}


	public void setDefaultText(String inputText) {
		this.inputText = inputText;
	}


	public int getRowHeight() {
		return rowHeight;
	}


	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	public List<TextSolution> getTextSolutions() {
		return solutions;
	}

	public void setTextSolutions(List<TextSolution> solutions) {
		this.solutions = solutions;
	}

	public void addTextSolution(TextSolution solution) {
		this.solutions.add(solution);
	}
}
