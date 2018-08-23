package ch.uzh.marugoto.core.data.entity;

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
	private String placeholderText;
	private String defaultText;
	private int rowHeight;
	
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
	 * @param defaultText
	 */
	public TextExercise(int x, int y, int width, int height, int minLength, int maxLength, String placeholderText, String defaultText) {
		super(x, y, width, height);
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.placeholderText = placeholderText;
		this.defaultText = defaultText;
		this.rowHeight = TextExercise.DEFAULT_ROW_HEIGHT;
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
	 * @param defaultText
	 * @param rowHeight
	 */
	public TextExercise(int x, int y, int width, int height, int minLength, int maxLength, String placeholderText, String defaultText, int rowHeight) {
		this(x, y, width, height, minLength, maxLength, placeholderText, defaultText);
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


	public int getRowHeight() {
		return rowHeight;
	}


	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}
}