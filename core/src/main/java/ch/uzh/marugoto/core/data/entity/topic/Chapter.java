package ch.uzh.marugoto.core.data.entity.topic;

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
	private String id;
	private String title;
	private ImageResource icon;

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ImageResource getIcon() {
		return icon;
	}

	public void setIcon(ImageResource image) {
		this.icon = image;
	}

	public Chapter() {
		super();
	}

	@PersistenceConstructor
	public Chapter(String title) {
		this();
		this.title = title;
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