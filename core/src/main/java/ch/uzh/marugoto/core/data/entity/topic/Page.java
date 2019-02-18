package ch.uzh.marugoto.core.data.entity.topic;

import java.time.Duration;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Holds information which will be shown.
 * 
 */
@Document
@JsonIgnoreProperties({ "id", "time" })
public class Page {
	@Id
	private String id;
	private String title;
	private boolean continueRandomly;
	private boolean timerVisible;
	private boolean notebookVisible;
	private boolean autoTransitionOnTimerExpiration;
	private boolean endOfTopic;
	private Money money;
	private VirtualTime time;
	@Ref
	private Chapter chapter;

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

	public Page(String title, Chapter chapter, VirtualTime time, Money money) {
		this(title, chapter);
		this.time = time;
		this.money = money;
	}

	public Page(String title, Chapter chapter, VirtualTime time, Money money, boolean continueRandomly,
				boolean isTimerVisible, boolean isNotebookVisible, boolean autoTransitionOnTimerExpiration) {
		this(title, chapter, time, money);
		this.continueRandomly = continueRandomly;
		this.timerVisible = isTimerVisible;
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

	public boolean isTimerVisible() {
		return timerVisible;
	}

	public void setTimerVisible(boolean isTimerVisible) {
		this.timerVisible = isTimerVisible;
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

	public boolean isEndOfTopic() {
		return endOfTopic;
	}

	public void setEndOfTopic(boolean endOfTopic) {
		this.endOfTopic = endOfTopic;
	}

	public Money getMoney() {
		return money;
	}

	public void setMoney(Money money) {
		this.money = money;
	}

	public VirtualTime getTime() {
		return time;
	}

	public void setTime(VirtualTime time) {
		this.time = time;
	}

	public void setTime(Duration duration) {
		this.time = new VirtualTime(duration, false);
	}

	public Long getTimeLimit() {
		Long timeLimit = null;

		if (time != null) {
			timeLimit = time.getTime().toSeconds();
		}

		return timeLimit;
	}

	public Chapter getChapter() {
		return chapter;
	}

	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
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