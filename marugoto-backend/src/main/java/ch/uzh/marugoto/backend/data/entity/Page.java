package ch.uzh.marugoto.backend.data.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndex;

/**
 * business model page
 * 
 * @author Christian
 */

@Document("pages")
@HashIndex(fields = { "title" }, unique = true)
public class Page {

	@Id
	private String id;
	private String title;
	private boolean isActive;
	private int timeLimit;
	private boolean isTimerVisible;
	private boolean isEndOfStory;
	private boolean isNotebookOpen;
	private boolean autoTransitionOnTimeExpiration;

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public boolean isActive() {
		return isActive;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public boolean isTimerVisible() {
		return isTimerVisible;
	}

	public boolean isEndOfStory() {
		return isEndOfStory;
	}

	public boolean isNotebookOpen() {
		return isNotebookOpen;
	}

	public boolean autoTransitionOnTimeExpiration() {
		return autoTransitionOnTimeExpiration;
	}

	public Page(final String title, final boolean isActive) {
		super();
		this.title = title;
		this.isActive = isActive;
	}

}