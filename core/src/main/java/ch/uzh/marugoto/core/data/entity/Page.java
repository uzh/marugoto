package ch.uzh.marugoto.core.data.entity;

import java.time.Duration;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Holds the information which will be shown. It holds the components.
 * 
 */
@Document
@JsonIgnoreProperties({ "id", "storyline" })
public class Page {
	@Id
	private String id;
	private String title;
	private boolean continueRandomly;
	private VirtualTime timeLimit;
	private boolean timerVisible;
	private boolean endOfStory;
	private boolean notebookVisible;
	private boolean autoTransitionOnTimerExpiration;
	private VirtualTime time;
	private Money money;
	@Ref
	private Chapter chapter;
	@Ref
	private Storyline storyline;

	public Page() {
		super();
	}

	@PersistenceConstructor
	public Page(String title) {
		this();
		this.title = title;
	}

	public Page(String title, Chapter chapter) {
		this(title);
		this.chapter = chapter;
	}

	public Page(String title, Chapter chapter, Storyline storyline, boolean isEndOfStory) {
		this(title, chapter);
		this.storyline = storyline;
		this.endOfStory = isEndOfStory;
	}


	public Page(String title, Chapter chapter,Storyline storyline, boolean continueRandomly, Duration timeLimit,
			boolean isTimerVisible, boolean isEndOfStory, boolean isNotebookVisible, boolean autoTransitionOnTimerExpiration) {
		this(title, chapter, storyline, isEndOfStory);
		this.continueRandomly = continueRandomly;
		this.timeLimit = new VirtualTime(timeLimit, false);
		this.timerVisible = isTimerVisible;
		this.endOfStory = isEndOfStory;
		this.notebookVisible = isNotebookVisible;
		this.autoTransitionOnTimerExpiration = autoTransitionOnTimerExpiration;
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

	public boolean isContinueRandomly() {
		return continueRandomly;
	}

	public void setContinueRandomly(boolean continueRandomly) {
		this.continueRandomly = continueRandomly;
	}

	public VirtualTime getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Duration timeLimit) {
		this.timeLimit = new VirtualTime(timeLimit, true);
	}

	public boolean isTimerVisible() {
		return timerVisible;
	}

	public void setTimerVisible(boolean isTimerVisible) {
		this.timerVisible = isTimerVisible;
	}

	public boolean isEndOfStory() {
		return endOfStory;
	}

	public void setEndOfStory(boolean isEndOfStory) {
		this.endOfStory = isEndOfStory;
	}

	public boolean isNotebookVisible() {
		return notebookVisible;
	}

	public void setNotebookVisible(boolean isNotebookVisible) {
		this.notebookVisible = isNotebookVisible;
	}

	public boolean isAutoTransitionOnTimerExpiration() {
		return autoTransitionOnTimerExpiration;
	}

	public void setAutoTransitionOnTimerExpiration(boolean autoTransitionOnTimerExpiration) {
		this.autoTransitionOnTimerExpiration = autoTransitionOnTimerExpiration;
	}

	public Chapter getChapter() {
		return chapter;
	}

	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}

	public Storyline getStoryline() {
		return storyline;
	}

	public void setStoryline(Storyline storyline) {
		this.storyline = storyline;
	}

	public VirtualTime getVirtualTime() {
		return time;
	}

	public void setVirtualTime(VirtualTime time) {
		this.time = time;
	}

	public Money getMoney() {
		return money;
	}

	public void setMoney(Money money) {
		this.money = money;
	}

	@Override
	public boolean equals(Object o) {
		boolean equals = false;

		if (o instanceof Page) {
			Page page = (Page) o;
			equals = id.equals(page.id);
		}

		return equals;
	}
}