package ch.uzh.marugoto.core.exception;

public class DtoToEntityException extends Exception {

    private static final long serialVersionUID = 1L;

    public DtoToEntityException() {
        super();
    }

    public DtoToEntityException(String message) {
        super(message);
    }
}
