package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * Text component
 * 
 */
public class TextComponent extends Component {
	private String markdownContent;

	@PersistenceConstructor
	public TextComponent(int numberOfColumns, String markdownContent) {
		super(numberOfColumns);
		this.markdownContent = markdownContent;
	}

	public TextComponent(int numberOfColumns, String markdownContent, Page page) {
		super(numberOfColumns, page);
		this.markdownContent = markdownContent;
	}

	public String getMarkdownContent() {
		return markdownContent;
	}

	public void setMarkdownContent(String markdownContent) {
		this.markdownContent = markdownContent;
	}
}
