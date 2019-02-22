package ch.uzh.marugoto.backend.exception;

import org.springframework.validation.FieldError;

import java.util.List;

public class RequestValidationException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private List<FieldError> fieldErrors;

	public RequestValidationException(String message) {
	    super (message);
	}

	public RequestValidationException(String message, List<FieldError> errorList) {
		this(message);
		this.fieldErrors = errorList;
	}

	public RequestValidationException(List<FieldError> errorList) {
		super("validation.errors");
		this.fieldErrors = errorList;
	}

	public List<FieldError> getFieldErrors() {
		return fieldErrors;
	}
}
