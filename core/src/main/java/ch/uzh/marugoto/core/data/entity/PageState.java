package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *  Page state - should contain information related to page state for user 
 */
@Document
@JsonIgnoreProperties({"page", "storylineState", "user"})
public class PageState {
	@Id
	private String id;
	private LocalDateTime enteredAt;
	private LocalDateTime leftAt;
	private List<PageTransitionState> pageTransitionStates;
	private List<NotebookEntry> notebookEntries;
	@Ref
	private StorylineState storylineState;
	@Ref(lazy = true)
	private User user;
	@Ref
	private Page page;

	@PersistenceConstructor
	public PageState(Page page, User user) {
		super();
		this.page = page;
		this.user = user;
		this.enteredAt = LocalDateTime.now();
		this.pageTransitionStates = new ArrayList<>();
		this.notebookEntries = new ArrayList<>();
	}

	public PageState(Page page, User user, StorylineState storylineState) {
		this(page, user);
		this.storylineState = storylineState;
	}

	public String getId() {
		return id;
	}

	public LocalDateTime getEnteredAt() {
		return enteredAt;
	}

	public void setEnteredAt(LocalDateTime enteredAt) {
		this.enteredAt = enteredAt;
	}

	public LocalDateTime getLeftAt() {
		return leftAt;
	}

	public void setLeftAt(LocalDateTime leftAt) {
		this.leftAt = leftAt;
	}

	public List<PageTransitionState> getPageTransitionStates() {
		return pageTransitionStates;
	}

	public void addPageTransitionState(PageTransitionState pageTransitionState) {
		this.pageTransitionStates.add(pageTransitionState);
	}

	public void setPageTransitionStates(List<PageTransitionState> pageTransitionStates) {
		this.pageTransitionStates = pageTransitionStates;
	}

	public List<NotebookEntry> getNotebookEntries() {
		return notebookEntries;
	}

	public void addNotebookEntry(NotebookEntry notebookEntry) {
		this.notebookEntries.add(notebookEntry);
	}

	public void setNotebookEntries(List<NotebookEntry> notebookEntries) {
		this.notebookEntries = notebookEntries;
	}

	public StorylineState getStorylineState() {
		return storylineState;
	}

	public void setStorylineState(StorylineState storylineState) {
		this.storylineState = storylineState;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
