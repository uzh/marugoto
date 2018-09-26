package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;

/**
 * Text component
 * 
 */

@Document
public class TextComponent extends Component {

	private String markdownContent;

	public TextComponent(int numberOfColumns, int height, String title, String text) {
		super(numberOfColumns, height);
		this.title = title;
		this.text = text;
	}
	public String getMarkdownContent() {
		return markdownContent;
	}

	public void setMarkdownContent(String markdownContent) {
		this.markdownContent = markdownContent;
	}
}
