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

	public PageTransitionState() {
		super();
	}

	public PageTransitionState(boolean isAvailable, boolean chosenByPlayer, PageTransition pageTransition) {
		super();
		this.isAvailable = isAvailable;
		this.chosenByPlayer = chosenByPlayer;
		this.pageTransition = pageTransition;
	}
}
