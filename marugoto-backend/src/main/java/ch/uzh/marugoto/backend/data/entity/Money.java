package ch.uzh.marugoto.backend.data.entity;

/**
 * Represents virtual game money.
 * 
 * Negative or positive amount values can be represented by a signed value
 * (+/-).
 * 
 * If absolute, the account will be reset to the given amount, otherwise the
 * given amount value will be subtracted/added to the current account value.
 */
public class Money {
	private double amount;
	private boolean isAbsolute;

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public boolean isAbsolute() {
		return isAbsolute;
	}

	public void setAbsolute(boolean isAbsolute) {
		this.isAbsolute = isAbsolute;
	}

	public Money() {
		super();
	}

	public Money(double amount, boolean isAbsolute) {
		super();
		this.amount = amount;
		this.isAbsolute = isAbsolute;
	}
}
