package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;

/**
 * Text component
 * 
 */

@Document
public class TextComponent extends Component {
	@Id
	private String id;
	private String text;

	public TextComponent(int x, int y, int width, int height, String text) {
		super(x, y, width, height);
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
}
