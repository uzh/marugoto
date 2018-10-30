package ch.uzh.marugoto.core.exception;

import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.Messages;

public class PageTransitionNotFoundException extends Exception {

	@Autowired
	private static Messages messages;
    private static final long serialVersionUID = 1L;

    public PageTransitionNotFoundException() {
        super(messages.get("transitionNotFound"));
    }
}
