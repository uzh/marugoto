package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;

/**
 * Text component
 * 
 */

@Document
public class TextComponent extends Component {

	private String title;
	private String text;

	public TextComponent(int numberOfColumns, int height, String title, String text) {
		super(numberOfColumns, height);
		this.title = title;
		this.text = text;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
