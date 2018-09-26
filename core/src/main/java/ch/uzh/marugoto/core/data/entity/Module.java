package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

/**
 * The Module is the starting point of the game.
 */

@Document
public class Module {
	
	@Id
	private String Id;
	private String title;
	private String icon;
	boolean isActive;
	

	@Ref
	private Page page;
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public Module () {
		super();
	} 
	
	public Module (String title, String icon, boolean isActive, Page page) {
		super();
		this.title = title;
		this.icon = icon;
		this.isActive = isActive;
		this.page = page;
	}
	
}
