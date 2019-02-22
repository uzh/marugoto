package ch.uzh.marugoto.backend.resource;

import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiError {
	private String message;
	private String debugMessage;
	private String path;
	private String exception;
	private String file;
	private String stackTrace;
	private Map<String, String> errorList = new HashMap<>();
	private ApiError innerException;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Map<String, String> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<FieldError> errorList) {
		if (errorList != null) {
			for (FieldError error : errorList) {
				this.errorList.put(error.getField(), error.getDefaultMessage());
			}
		}
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public ApiError getInnerException() {
		return innerException;
	}

	public void setInnerException(ApiError innerException) {
		this.innerException = innerException;
	}
}
