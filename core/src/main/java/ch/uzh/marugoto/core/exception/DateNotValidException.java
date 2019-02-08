package ch.uzh.marugoto.core.exception;

public class DateNotValidException extends Exception {
    private static final long serialVersionUID = 1L;

    public DateNotValidException() {
        super();
    }

    public DateNotValidException(String message) {
        super(message);
    }
}
