package ch.uzh.marugoto.core.data.entity.state;

import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.TransitionChosenOptions;

/**
 * Class that will contain states related to page updateStatesAfterTransition
 */
public class PageTransitionState {
	private boolean isAvailable = false;
	@JsonIgnore
	private TransitionChosenOptions chosenBy;
	@Ref
	private PageTransition pageTransition;
	
	private PageTransitionState() {
		super();
		this.chosenBy = TransitionChosenOptions.none;
	}

	public PageTransitionState(PageTransition pageTransition) {
		this();
		this.pageTransition = pageTransition;
	}

	public PageTransitionState(PageTransition pageTransition, boolean isAvailable) {
		this(pageTransition);
		this.isAvailable = isAvailable;
	}
	
	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public TransitionChosenOptions getChosenBy() {
		return chosenBy;
	}

	public void setChosenBy(TransitionChosenOptions chosenBy) {
		this.chosenBy = chosenBy;
	}

	public PageTransition getPageTransition() {
		return pageTransition;
	}

	public void setPageTransition(PageTransition pageTransition) {
		this.pageTransition = pageTransition;
	}
}
