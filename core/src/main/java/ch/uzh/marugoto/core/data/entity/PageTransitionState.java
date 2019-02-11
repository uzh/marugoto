package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class that will contain page transition availability state
 */
public class PageTransitionState {

	private boolean isAvailable;
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

	@JsonIgnore
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

	@JsonGetter
	public boolean isVisible() {
		return pageTransition.getButtonText() != null && isAvailable;
	}
}
