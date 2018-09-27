package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;

/**
 * Text component
 * 
 */

@Document
public class TextComponent extends Component {

	private String markdownContent;

	public TextComponent(int numberOfColumns, String markdownContent) {
		super(numberOfColumns);
		this.markdownContent = markdownContent;
	}

	public String getMarkdownContent() {
		return markdownContent;
	}

	public void setMarkdownContent(String markdownContent) {
		this.markdownContent = markdownContent;
	}
}
