package ch.uzh.marugoto.core.data.entity.topic;

import org.springframework.data.annotation.Transient;

import ch.uzh.marugoto.core.data.entity.state.ExerciseState;

/**
 *
 * Base class for all exercises
 * 
 */
abstract public class Exercise extends Component {
	@Transient
	private ExerciseState exerciseState;
	public Exercise() {
		super();
	}
	public Exercise(int numberOfColumns) {
		super(numberOfColumns);
	}
	public Exercise(int numberOfColumns, Page page) {
		super(numberOfColumns, page);
	}

	public ExerciseState getExerciseState() {
		return exerciseState;
	}

	public void setExerciseState(ExerciseState exerciseState) {
		this.exerciseState = exerciseState;
	}
}
