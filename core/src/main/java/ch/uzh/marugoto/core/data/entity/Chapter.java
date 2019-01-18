package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * Pages can be structured through chapters.
 *  		
 */
@Document
public class Chapter {
	@Id
	@JsonIgnore
	private String id;
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

	@PersistenceConstructor
	public Chapter(String title, String icon) {
		this();
		this.title = title;
		this.icon = icon;
	}

	@Override
	public boolean equals(Object o) {
		boolean equals = false;

		if (o instanceof Chapter) {
			Chapter chapter = (Chapter) o;
			equals = id.equals(chapter.id);
		}

		return equals;
	}
}