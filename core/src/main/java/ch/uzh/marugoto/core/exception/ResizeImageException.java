package ch.uzh.marugoto.core.exception;

public class ResizeImageException extends Throwable {
    private static final long serialVersionUID = 1L;

    public ResizeImageException() {
        super();
    }

    public ResizeImageException(String message) {
        super(message);
    }

    public ResizeImageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
