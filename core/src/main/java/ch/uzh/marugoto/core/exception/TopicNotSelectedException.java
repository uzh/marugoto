package ch.uzh.marugoto.core.exception;

public class TopicNotSelectedException extends Exception {
    private static final long serialVersionUID = 1L;

    public TopicNotSelectedException() {
        super();
    }

    public TopicNotSelectedException(String message) {
        super(message);
    }

    public TopicNotSelectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
