package ch.uzh.marugoto.core.data.entity;

public class DateSolution {
	private String correctDate;

	public String getCorrectDate() {
		return correctDate;
	}

	public void setCorrectDate(String correctDate) {
		this.correctDate = correctDate;
	}
	
	public DateSolution() {
		super();
	}

	public DateSolution(String correctDate) {
		super();
		this.correctDate = correctDate;
	}
}
