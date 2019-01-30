package ch.uzh.marugoto.core.exception;

public class RepliedMailNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public RepliedMailNotFoundException() {
        super();
    }

    public RepliedMailNotFoundException(String message) {
        super(message);
    }
}
