package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

/**
 * Class that will contain states related to page transition
 */
@Document
public class PageTransitionState {
	@Id
	private String id;
	private boolean isAvailable;
	private boolean chosenByPlayer;
	@Ref
	private PageTransition pageTransition;
	@Ref
	private User user;
	
	public PageTransitionState() {
		super();
	}

	public PageTransitionState(boolean isAvailable, PageTransition pageTransition, User user) {
		super();
		this.isAvailable = isAvailable;
		this.pageTransition = pageTransition;
		this.user = user;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public boolean isChosenByPlayer() {
		return chosenByPlayer;
	}

	public void setChosenByPlayer(boolean chosenByPlayer) {
		this.chosenByPlayer = chosenByPlayer;
	}

	public PageTransition getPageTransition() {
		return pageTransition;
	}

	public void setPageTransition(PageTransition pageTransition) {
		this.pageTransition = pageTransition;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
