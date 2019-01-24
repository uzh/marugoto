package ch.uzh.marugoto.core.data.entity;

import java.time.Duration;

/**
 * Represents virtual game time.
 *
 * Negative or positive time values can be represented by a signed value (+/-).
 *
 * If absolute, the account time will be reset to the given time value,
 * otherwise the given duration will be subtracted/added to the current account
 * time.
 */
public class VirtualTime {
	private long time;
	private boolean isAbsolute;

	public Duration getTime() {
		return Duration.ofSeconds(time);
	}

	public void setTime(Duration time) {
		this.time = time.toSeconds();
	}

	public boolean isAbsolute() {
		return isAbsolute;
	}

	public void setAbsolute(boolean isAbsolute) {
		this.isAbsolute = isAbsolute;
	}

	public VirtualTime() {
		super();
		this.time = 0;
	}

	public VirtualTime(Duration time, boolean isAbsolute) {
		super();
		this.time = time.toSeconds();
		this.isAbsolute = isAbsolute;
	}
}
