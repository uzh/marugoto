package ch.uzh.marugoto.core.exception;

public class ResourceTypeResolveException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final String defaultMessage = "Provided resource type is not valid!";

    public ResourceTypeResolveException() {
        super(defaultMessage);
    }
}
