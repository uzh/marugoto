package ch.uzh.marugoto.core.exception;

public class PageStateNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public PageStateNotFoundException() {
        super("Page state not exist for user");
    }
}
