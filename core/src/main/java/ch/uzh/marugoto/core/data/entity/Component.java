package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;

/**
 * 
 * Base Component entity
 *
 */
abstract public class Component {
	@Id
	private String id;
	private int numberOfColumns;
	
	public Component() {
		super();
	}

	public Component(int numberOfColumns) {
		this();
		this.numberOfColumns = numberOfColumns;
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
}
