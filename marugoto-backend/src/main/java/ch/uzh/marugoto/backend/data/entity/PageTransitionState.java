package ch.uzh.marugoto.backend.data.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

@Document
public class PageTransitionState {

	@Id
	private String id;

	/**
	 *  if transition is available or locked
	 */
	private boolean isAvailable;
	
	/**
	 *  if is chosen by player or automatically 
	 */
	private boolean isPlayer;
	
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


	public boolean isPlayer() {
		return isPlayer;
	}


	public void setPlayer(boolean isPlayer) {
		this.isPlayer = isPlayer;
	}
	

	public PageTransition getPageTransition() {
		return pageTransition;
	}

	
	public void setPageTransition(PageTransition pageTransition) {
		this.pageTransition = pageTransition;
	}
	
	
	/**
	 *  Default constructor
	 */

	public PageTransitionState() {
		super();
	}
	
	public PageTransitionState(boolean isAvailable, boolean isPlayer, PageTransition pageTransition) {
		super();
		this.isAvailable = isAvailable;
		this.isPlayer = isPlayer;
		this.pageTransition = pageTransition;

	}	
}
