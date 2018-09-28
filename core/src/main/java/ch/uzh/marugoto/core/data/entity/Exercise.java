package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.PersistenceConstructor;

/**
 *
 * Base class for all exercises
 * 
 */

abstract public class Exercise extends Component {
	public Exercise(int numberOfColumns) {
		super(numberOfColumns);
	}
}
