package ch.uzh.marugoto.core.exception;

public class PageTransitionNotAllowedException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6284816991024090750L;

	public PageTransitionNotAllowedException() {
        super("Page transition not allowed!");
    }
}