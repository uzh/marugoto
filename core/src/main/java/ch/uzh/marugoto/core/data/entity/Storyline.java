package ch.uzh.marugoto.core.data.entity;

import java.time.Duration;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;

/**
 * The Storyline is the story or game the user is playing.
 *
 */
@Document
public class Storyline {
	
	@Id
	private String Id;
	private String title;
	private String icon;
	private Duration virtualTimeLimit;
	boolean isActive;
	
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

	public Storyline () {
		super();
	}
	
	public Storyline (String title, String icon, Duration virtualTimeLimit, boolean isActive ) {
		super();
		this.title = title;
		this.icon = icon;
		this.virtualTimeLimit = virtualTimeLimit;
		this.isActive = isActive;
	}

}

