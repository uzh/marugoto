package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * Base Component entity
 *
 */
@Document("component")
@JsonIgnoreProperties({"page"})
abstract public class Component {
	@Id
	private String id;
	private int numberOfColumns;
	@Ref
	private Page page;

	@PersistenceConstructor
	public Component(int numberOfColumns) {
		super();
		this.numberOfColumns = numberOfColumns;
	}

	public Component(int numberOfColumns, Page page) {
		this(numberOfColumns);
		this.page = page;
	}

	public String getId() {
		return id;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
