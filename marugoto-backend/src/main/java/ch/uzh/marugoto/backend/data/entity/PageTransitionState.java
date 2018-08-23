package ch.uzh.marugoto.backend.data.entity;

import java.time.LocalDateTime;

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
	 *  if transition is chosen by player or automatically 
	 */
	private boolean isPlayer;

	/**
	 *  Beginning time 
	 */
	private LocalDateTime startedAt;
	
	/**
	 *  End time  
	 */
	private LocalDateTime finishAt;
	
	
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
	

	public LocalDateTime getStartedAt() {
		return startedAt;
	}


	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}


	public LocalDateTime getFinishAt() {
		return finishAt;
	}


	public void setFinishAt(LocalDateTime finishAt) {
		this.finishAt = finishAt;
	}	
	
	/**
	 *  Default constructor
	 */

	public PageTransitionState() {
		super();
		this.startedAt = LocalDateTime.now();

	}
	
	public PageTransitionState(boolean isAvailable, boolean isPlayer, PageTransition pageTransition) {
		super();
		this.isAvailable = isAvailable;
		this.isPlayer = isPlayer;
		this.pageTransition = pageTransition;

	}

}
