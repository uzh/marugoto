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
	private boolean choosedByPlayer;
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

	public boolean isChoosedByPlayer() {
		return choosedByPlayer;
	}

	public void setChoosedByPlayer(boolean choosedByPlayer) {
		this.choosedByPlayer = choosedByPlayer;
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

	public PageTransitionState(boolean isAvailable, boolean choosedByPlayer, PageTransition pageTransition) {
		super();
		this.isAvailable = isAvailable;
		this.choosedByPlayer = choosedByPlayer;
		this.pageTransition = pageTransition;

	}

}
