package ch.uzh.marugoto.core.data.entity;

/**
 *
 * Base class for all exercises
 * 
 */
abstract public class Exercise extends Component {
	public Exercise(int numberOfColumns) {
		super(numberOfColumns);
	}
	public Exercise(int numberOfColumns, Page page) {
		super(numberOfColumns, page);
	}
}
