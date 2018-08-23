package ch.uzh.marugoto.core.data.entity;

/**
 *
 * Base class for all exercises
 * 
 */

abstract public class Exercise extends Component {
	
	public Exercise() {
		super();
	}

	public Exercise(int x, int y, int width, int height) {
		super(x, y , width, height);
	}
}
