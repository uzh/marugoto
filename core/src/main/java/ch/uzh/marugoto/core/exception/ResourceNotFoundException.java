package ch.uzh.marugoto.core.exception;

@SuppressWarnings("serial")
public class ResourceNotFoundException extends Exception {
    
	public ResourceNotFoundException(String resourceUrl) {
        super(String.format("Resource not exists: %s", resourceUrl));
    }
}
