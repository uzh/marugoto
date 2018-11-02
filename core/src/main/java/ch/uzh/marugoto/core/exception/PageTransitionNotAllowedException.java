package ch.uzh.marugoto.core.exception;

public class PageTransitionNotAllowedException extends Exception {

	private static final long serialVersionUID = -6284816991024090750L;
	private static final String defaultMessage = "Page transition not allowed";

	public PageTransitionNotAllowedException() {
		super(defaultMessage);
	}

	public PageTransitionNotAllowedException(String reason) {
		super(defaultMessage + ": " + reason);
	}
}
