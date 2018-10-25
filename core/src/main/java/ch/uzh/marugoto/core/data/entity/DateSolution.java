package ch.uzh.marugoto.core.data.entity;

import java.time.LocalDateTime;

public class DateSolution {
	private LocalDateTime correctDate;

	public LocalDateTime getCorrectDate() {
		return correctDate;
	}

	public void setCorrectDate(LocalDateTime correctDate) {
		this.correctDate = correctDate;
	}
	
	public DateSolution() {
		super();
	}

	public DateSolution(LocalDateTime correctDate) {
		super();
		this.correctDate = correctDate;
	}
}
