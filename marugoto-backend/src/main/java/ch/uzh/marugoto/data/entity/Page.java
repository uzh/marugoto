package ch.uzh.marugoto.data.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndex;

@Document("pages")
@HashIndex(fields = { "title" }, unique = true)
public class Page {

	@Id
	private String id;

	private String title;
	private boolean isActive;

	public String getId() {
		return id;
	}

	public Page() {
		super();
	}

	public Page(final String title, final boolean isActive) {
		super();
		this.title = title;
		this.isActive = isActive;
	}

	// getter & setter

	@Override
	public String toString() {
		return "Page [id=" + id + ", title=" + title + ", isActive=" + isActive + "]";
	}

}