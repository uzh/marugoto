package ch.uzh.marugoto.core.data.entity.topic;

import java.time.LocalDate;

public class DateSolution {
	private LocalDate correctDate;

	public LocalDate getCorrectDate() {
		return correctDate;
	}

	public void setCorrectDate(LocalDate correctDate) {
		this.correctDate = correctDate;
	}
	
	public DateSolution() {
		super();
	}

	public DateSolution(LocalDate correctDate) {
		super();
		this.correctDate = correctDate;
	}
}
