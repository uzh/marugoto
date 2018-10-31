package ch.uzh.marugoto.core.exception;

import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.Messages;

public class PageStateNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    @Autowired
    private static Messages messages;
	
    public PageStateNotFoundException() {
        super(messages.get("pageStateNotFound"));
    }
}
