package ch.uzh.marugoto.core.exception;

public class GameStateBrokenException extends Exception {
    private static final long serialVersionUID = 1L;

    public GameStateBrokenException() {
        super();
    }

    public GameStateBrokenException(String message) {
        super(message);
    }

    public GameStateBrokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
