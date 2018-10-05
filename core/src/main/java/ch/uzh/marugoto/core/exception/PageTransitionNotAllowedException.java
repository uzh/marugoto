package ch.uzh.marugoto.core.exception;

public class PageTransitionNotAllowedException extends Exception {
    public PageTransitionNotAllowedException() {
        super("Page transition not allowed!");
    }
}
