package ch.uzh.marugoto.core.data.entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndexed;
import com.arangodb.springframework.annotation.Ref;

/**
 * Holds the information which will be shown. It holds the Components,
 * VirtualTime and Money.
 * 
 */
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
	private boolean isNotebookVisible;
	private boolean autoTransitionOnTimerExpiration;
	private List<Component> components;
	private List<PageTransition> pageTransitions;
	private VirtualTime time;
	private Money money;

	@Ref
	private Chapter chapter;
	
	@Ref
	private Storyline startsStoryline;
	

	public Page() {
		super();
	}
	
	public Page(String title, boolean isActive, Chapter chapter) {
		this();
		this.title = title;
		this.isActive = isActive;
		this.chapter = chapter;
		this.components = new ArrayList<>();
	}

	public Page(String title, boolean isActive, Chapter chapter, Storyline storyline, boolean isEndOfStory) {
		this(title, isActive, chapter);
		this.startsStoryline = storyline;
		this.isEndOfStory = isEndOfStory;
	}


	public Page(String title, boolean isActive, Chapter chapter,Storyline storyline, boolean continueRandomly, Duration timeLimit,
			boolean isTimerVisible, boolean isEndOfStory, boolean isNotebookVisible, boolean autoTransitionOnTimerExpiration) {
		this(title, isActive, chapter, storyline, isEndOfStory);
		this.continueRandomly = continueRandomly;
		this.timeLimit = timeLimit;
		this.isTimerVisible = isTimerVisible;
		this.isEndOfStory = isEndOfStory;
		this.isNotebookVisible = isNotebookVisible;
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

	public boolean isNotebookVisible() {
		return isNotebookVisible;
	}

	public void setIsNotebookVisible(boolean isNotebookVisible) {
		this.isNotebookVisible = isNotebookVisible;
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

	public Storyline getStartsStoryline() {
		return startsStoryline;
	}

	public void setStartsStoryline(Storyline storyline) {
		this.startsStoryline = storyline;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public void addComponent(Component component) {
		this.components.add(component);
	}

	public List<PageTransition> getPageTransitions() {
		return pageTransitions;
	}

	public void setPageTransitions(List<PageTransition> pageTransitions) {
		this.pageTransitions = pageTransitions;
	}

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
}