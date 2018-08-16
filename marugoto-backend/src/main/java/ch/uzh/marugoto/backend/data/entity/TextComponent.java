package ch.uzh.marugoto.backend.data.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;

/**
 * Simple TextComponent which holds the text.
 *
 */
@Document
public class TextComponent extends Component {
	@Id
	private String id;
	private String text;

	public TextComponent(int x, int y, int width, int height, Page page, String text) {
		super(x, y, width, height, page);
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		return "Text [id=" + id + ", text=" + text + "," + page + "]";
	}
}
