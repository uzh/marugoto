package ch.uzh.marugoto.core.exception;

import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.Messages;

public class PageTransitionNotAllowedException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6284816991024090750L;
	@Autowired
	private static Messages messages;

	public PageTransitionNotAllowedException() {
        super(messages.get("transitionNotAllowed"));
    }

	public PageTransitionNotAllowedException(String reason) {
		super(messages.get("transitionNotAllowed") + reason);
	}
}
