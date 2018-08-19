package ch.uzh.marugoto.backend.resource;

public class ApiError {
	private String message;
	private String debugMessage;
	private String path;
	private String exception;
	private String file;
	private String stackTrace;
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
