package ch.uzh.marugoto.core.data.entity.topic;

/**
 * Date exercise
 */
public class DateExercise extends Exercise{
	private DateSolution solution;
	
	public DateSolution getSolution() {
		return solution;
	}

	public void setSolution(DateSolution solution) {
		this.solution = solution;
	}
	
	public DateExercise () {
		super();
	}

	public DateExercise(int numberOfColumns, DateSolution solution, Page page) {
		super(numberOfColumns, page);
		this.solution = solution;
	}

}
