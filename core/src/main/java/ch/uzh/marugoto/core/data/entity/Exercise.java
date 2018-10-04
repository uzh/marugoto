package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;

/**
 *
 * Base class for all exercises
 * 
 */

abstract public class Exercise extends Component {
	@Id
	private String id;

	public Exercise(int numberOfColumns) {
		super(numberOfColumns);
	}

	public String getId() {
		return id;
	}
}
