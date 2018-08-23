package ch.uzh.marugoto.backend.data.entity;

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
	private String title;
	private String text;

	public TextComponent(int x, int y, int width, int height, String title, String text) {
		super(x, y, width, height);
		this.title = title;
		this.text = text;
	}
	
	public String getId() {
		return id;
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
