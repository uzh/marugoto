/**
 * 
 */
package ch.uzh.marugoto.backend.data.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

/**
 *  Page state - should have information of the page 
 */

@Document
public class PageState {
	
	@Id
	private String id;
	private LocalDateTime enteredAt;
	private LocalDateTime leftAt;
	
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

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
