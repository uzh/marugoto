package ch.uzh.marugoto.core.exception;

public class PageTransitionNotFoundException extends Exception {
    private static final long serialVersionUID = 2L;
    private static final String defaultMessage = "Page transition not found";

    public PageTransitionNotFoundException() {
        super(defaultMessage);
    }
}
