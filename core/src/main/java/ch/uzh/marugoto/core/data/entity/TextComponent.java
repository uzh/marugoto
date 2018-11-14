package ch.uzh.marugoto.core.data.entity;

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
