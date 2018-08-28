package ch.uzh.marugoto.core.data.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

/**
 *  Page state - should contain information related to page state for user 
 */

@Document
public class PageState {
	
	@Id
	private String id;
	private LocalDateTime enteredAt;
	private LocalDateTime leftAt;
	private List<PageTransitionState> pageTransitionStates;
	private List<ExerciseState> exerciseStates;
	@Ref
	private User user;
	@Ref
	private Page page;
	
	public PageState() {
		super();
		this.enteredAt = LocalDateTime.now();
		this.pageTransitionStates = new ArrayList<PageTransitionState>();
	}
	
	public PageState(Page page) {
		this();
		this.page = page;
	}
	
	public PageState(Page page, User user) {
		this(page);
		this.user = user;
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

	public List<ExerciseState> getExerciseStates() {
		return exerciseStates;
	}

	public void setExerciseStates(List<ExerciseState> exerciseStates) {
		this.exerciseStates = exerciseStates;
	}

	public void addExerciseState(ExerciseState exerciseState) {
		this.exerciseStates.add(exerciseState);
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

	public List<PageTransitionState> getPageTransitionStates() {
		return pageTransitionStates;
	}

	public void setPageTransitionStates(List<PageTransitionState> pageTransitionStates) {
		this.pageTransitionStates = pageTransitionStates;
	}

	public void addPageTransitionState (PageTransitionState pageTransitionState) {
		this.pageTransitionStates.add(pageTransitionState);
	}
}
