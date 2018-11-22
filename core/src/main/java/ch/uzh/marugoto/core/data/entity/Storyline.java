package ch.uzh.marugoto.core.data.entity;

import java.time.Duration;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndexed;

/**
 * The Storyline is the story or game the user is playing.
 *
 */
@Document
public class Storyline {
	@Id
	private String id;
	@HashIndexed(unique = true)
	private String title;
	private String icon;
	private Duration virtualTimeLimit;

	public Storyline() {
		super();
	}
	@PersistenceConstructor
	public Storyline (String title) {
		super();
		this.title = title;
	}

	public Storyline (String title, String icon, Duration virtualTimeLimit) {
		this(title);
		this.icon = icon;
		this.virtualTimeLimit = virtualTimeLimit;
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
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public Duration getVirtualTimeLimit() {
		return virtualTimeLimit;
	}

	public void setVirtualTimeLimit(Duration virtualTimeLimit) {
		this.virtualTimeLimit = virtualTimeLimit;
	}

	@Override
	public boolean equals(Object o) {
		boolean equals = false;

		if (o instanceof Storyline) {
			Storyline storyline = (Storyline) o;
			equals = id.equals(storyline.id);
		}

		return equals;
	}
}

