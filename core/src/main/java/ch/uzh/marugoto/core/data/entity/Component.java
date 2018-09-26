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
	private int height;
	
	public Component() {
		super();
	}

	public Component(int numberOfColumns, int height) {
		this();
		this.numberOfColumns = numberOfColumns;
		this.height = height;
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

	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

}
