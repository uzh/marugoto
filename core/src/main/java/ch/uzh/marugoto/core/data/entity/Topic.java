package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;

import java.util.Objects;

/**
 * The Topic is the starting point of the game.
 */
@Document
@JsonIgnoreProperties({"active", "startPage"})
public class Topic {
	@Id
	private String id;
	private String title;
	private String icon;
	private boolean active;
	@Ref
	private Page startPage;

	public Topic () {
		super();
	}

	public Topic (String title, String icon, boolean active, Page startPage) {
		super();
		this.title = title;
		this.icon = icon;
		this.active = active;
		this.startPage = startPage;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public Page getStartPage() {
		return startPage;
	}
	
	public void setStartPage(Page page) {
		this.startPage = page;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Topic topic = (Topic) o;
		return Objects.equals(id, topic.id);
	}
}
