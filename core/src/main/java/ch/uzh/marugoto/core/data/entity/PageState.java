package ch.uzh.marugoto.core.data.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *  Page state - should contain information related to page state for user 
 */
@Document
@JsonIgnoreProperties({"page"})
public class PageState {
	@Id
	private String id;
	private LocalDateTime enteredAt;
	private LocalDateTime leftAt;
	private List<PageTransitionState> pageTransitionStates;
	@Ref
	private StorylineState partOf;
	@Ref
	private Page page;
	
	public PageState() {
		super();
		this.enteredAt = LocalDateTime.now();
		this.pageTransitionStates = new ArrayList<>();
	}
	
	public PageState(Page page, StorylineState partOf) {
		this();
		this.page = page;
		this.partOf = partOf;
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

	public void setPageTransitionStates(List<PageTransitionState> pageTransitionStates) {
		this.pageTransitionStates = pageTransitionStates;
	}
	
	public void addPageTransitionState(PageTransitionState pageTransitionState) {
		this.pageTransitionStates.add(pageTransitionState);
	}
	
	public PageTransitionState getPageTransitionState(PageTransition pageTransition) {
		PageTransitionState matchedState = null;
		
		for( PageTransitionState pageTranditionState : pageTransitionStates) {
			if (pageTranditionState.getPageTransition().getId().equals(pageTransition.getId())) {
				matchedState = pageTranditionState;
				break;
			}
		}

		return matchedState;
	}

	public StorylineState getPartOf() {
		return partOf;
	}

	public void setPartOf(StorylineState partOf) {
		this.partOf = partOf;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
