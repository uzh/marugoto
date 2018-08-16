package ch.uzh.marugoto.backend.data.entity;

import java.time.Duration;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndexed;
import com.arangodb.springframework.annotation.Ref;

@Document
public class Page {

	@Id
	private String id;
	@HashIndexed(unique = true)
	private String title;
	private boolean isActive;
	private boolean continueRandomly;
	private Duration timeLimit;
	private boolean isTimerVisible;
	private boolean isEndOfStory;
	private boolean isNotebookVisibleOnEnter;
	private boolean autoTransitionOnTimerExpiration;
	private List<Object> components;
	private List<PageTransition> pageTransitions;
	@Ref
	private Chapter chapter;
	private VirtualTime time;
	private Money money;
	

	public VirtualTime getTime() {
		return time;
	}

	public void setTime(VirtualTime time) {
		this.time = time;
	}

	public Money getMoney() {
		return money;
	}

	public void setMoney(Money money) {
		this.money = money;
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

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isContinueRandomly() {
		return continueRandomly;
	}

	public void setContinueRandomly(boolean continueRandomly) {
		this.continueRandomly = continueRandomly;
	}

	public Duration getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Duration timeLimit) {
		this.timeLimit = timeLimit;
	}

	public boolean isTimerVisible() {
		return isTimerVisible;
	}

	public void setTimerVisible(boolean isTimerVisible) {
		this.isTimerVisible = isTimerVisible;
	}

	public boolean isEndOfStory() {
		return isEndOfStory;
	}

	public void setEndOfStory(boolean isEndOfStory) {
		this.isEndOfStory = isEndOfStory;
	}

	public boolean isNotebookVisibleOnEnter() {
		return isNotebookVisibleOnEnter;
	}

	public void setNotebookVisibleOnEnter(boolean isNotebookVisibleOnEnter) {
		this.isNotebookVisibleOnEnter = isNotebookVisibleOnEnter;
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

	public List<Object> getComponents() {
		return components;
	}

	public void setComponents(List<Object> components) {
		this.components = components;
	}

	public List<PageTransition> getPageTransitions() {
		return pageTransitions;
	}

	public void setPageTransitions(List<PageTransition> pageTransitions) {
		this.pageTransitions = pageTransitions;
	}

	public Page() {
		super();
	}

	public Page(String title, boolean isActive, Chapter chapter) {
		super();
		this.title = title;
		this.isActive = isActive;
		this.chapter = chapter;
	}

	public Page(String title, boolean isActive, Chapter chapter, boolean continueRandomly, Duration timeLimit,
			boolean isTimerVisible, boolean isEndOfStory, boolean isNotebookVisibleOnEnter,
			boolean autoTransitionOnTimerExpiration) {
		super();
		this.title = title;
		this.isActive = isActive;
		this.chapter = chapter;
		this.continueRandomly = continueRandomly;
		this.timeLimit = timeLimit;
		this.isTimerVisible = isTimerVisible;
		this.isEndOfStory = isEndOfStory;
		this.isNotebookVisibleOnEnter = isNotebookVisibleOnEnter;
		this.autoTransitionOnTimerExpiration = autoTransitionOnTimerExpiration;
	}
}