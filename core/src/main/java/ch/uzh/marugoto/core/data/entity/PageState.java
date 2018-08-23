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
	
	private List<PageTransitionState> pageTransitionState;

	private List<ExerciseState> exercisesState;
	@Ref
	private User user;
	@Ref
	private Page page;
	
	public PageState() {
		super();
		this.enteredAt = LocalDateTime.now();
		this.pageTransitionState = new ArrayList<PageTransitionState>();
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

	public List<ExerciseState> getExercisesState() {
		return exercisesState;
	}

	public void setExercisesState(List<ExerciseState> exercisesState) {
		this.exercisesState = exercisesState;
	}

	public void addExerciseState(ExerciseState exercisesState) {
		this.exercisesState.add(exercisesState);
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

	public List<PageTransitionState> getPageTransitionState() {
		return pageTransitionState;
	}

	public void setPageTransitionState(List<PageTransitionState> pageTransitionState) {
		this.pageTransitionState = pageTransitionState;
	}
	public void addPageTransitionState (PageTransitionState pageTransitionState) {
		this.pageTransitionState.add(pageTransitionState);
	}
}
