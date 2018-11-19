package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.Duration;

/**
 * The Storyline is the story or game the user is playing.
 *
 */
@Document
public class Storyline {
	@Id
	private String id;
	private String title;
	private String icon;
	private Duration virtualTimeLimit;
	private boolean isActive;

	public Storyline() {
		super();
	}
	
	@PersistenceConstructor
	public Storyline (String title, boolean isActive) {
		super();
		this.title = title;
		this.isActive = isActive;
	}

	public Storyline (String title, String icon, Duration virtualTimeLimit, boolean isActive ) {
		this(title, isActive);
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
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
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

