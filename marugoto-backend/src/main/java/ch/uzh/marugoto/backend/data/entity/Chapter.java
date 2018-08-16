package ch.uzh.marugoto.backend.data.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndexed;

/**
 * Pages can be structured through chapters.
 *  		
 */
@Document
public class Chapter {

	@Id
	private String id;
	@HashIndexed(unique = true)
	private String title;
	private String icon;

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Chapter() {
		super();
	}

	public Chapter(String title, String icon) {
		super();
		this.title = title;
		this.icon = icon;
	}
}