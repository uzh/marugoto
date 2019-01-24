package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;

import java.time.Duration;

/**
 * The Storyline is the story or game the user is playing.
 *
 */
@Document
public class Storyline {
	@Id
	@JsonIgnore
	private String id;
	private String title;
	private String icon;
	private VirtualTime virtualTimeLimit;

	public Storyline() {
		super();
	}

	public Storyline (String title) {
		this();
		this.title = title;
	}

	public Storyline (String title, String icon, Duration virtualTimeLimit) {
		this(title);
		this.icon = icon;
		this.virtualTimeLimit = new VirtualTime(virtualTimeLimit, true);
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
	
	public VirtualTime getVirtualTimeLimit() {
		return virtualTimeLimit;
	}

	public void setVirtualTimeLimit(Duration virtualTimeLimit) {
		this.virtualTimeLimit = new VirtualTime(virtualTimeLimit, true);
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

