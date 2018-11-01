package ch.uzh.marugoto.core.exception;

public class RequestValidationException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public RequestValidationException (String message) {
	    super (message);
	}
}
