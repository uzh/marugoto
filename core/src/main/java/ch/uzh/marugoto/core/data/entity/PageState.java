package ch.uzh.marugoto.core.data.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *  Page state - should contain information related to page state for user 
 */

@Document
@JsonIgnoreProperties({"page", "user"})
public class PageState {
	
	@Id
	private String id;
	private LocalDateTime enteredAt;
	private LocalDateTime leftAt;
	@Ref
	private User user;
	@Ref
	private Page page;
	
	public PageState() {
		super();
		this.enteredAt = LocalDateTime.now();
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
}
