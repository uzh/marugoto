package ch.uzh.marugoto.core.exception;

public class PageTransitionNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public PageTransitionNotFoundException() {
        super("Page transition not found!");
    }
}
