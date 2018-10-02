package ch.uzh.marugoto.core.data.entity;

/**
 * 
 * Base Component entity
 *
 */
abstract public class Component {
	private int numberOfColumns;

	public Component(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}
}
