package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;

/**
 *
 * Base class for all exercises
 * 
 */

public class Exercise extends Component {
	
	public Exercise() {
		super();
	}

	public Exercise(int x, int y, int width, int height) {
		super(x, y , width, height);
	}
}
