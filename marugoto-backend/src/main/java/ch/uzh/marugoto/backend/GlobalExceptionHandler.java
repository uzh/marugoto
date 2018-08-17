package ch.uzh.marugoto.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@ControllerAdvice  
@RestController
public class GlobalExceptionHandler {
	
	@Value("${spring.profiles.active}")
	private String activeProfile;
	
	
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

    @ExceptionHandler(value = Exception.class)  
    public ResponseEntity<ApiError> handleException(Exception e) {
    	var err = convertException(e);
    	
    	// Add requested controller path
    	var uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
    	if (uriBuilder != null) {
	    	var uriComponents = uriBuilder.build();
	    	if (uriComponents != null)
	    		err.setPath(uriComponents.getPath());
    	}
    	
    	if (this.activeProfile != "production") {
    		// Sensitive information, only if not in production mode
    		
	    	// Add filename and line number
	    	if (e.getStackTrace() != null && e.getStackTrace().length > 0)
	    		err.setFile(e.getStackTrace()[0].getFileName() + ":" + e.getStackTrace()[0].getLineNumber());
    	}
    	
        return new ResponseEntity<ApiError>(err, HttpStatus.BAD_REQUEST);
    }
    
    protected ApiError convertException(final Throwable throwable) {
    	var err = new ApiError();
    	err.setMessage(throwable.getMessage());
    	err.setException(throwable.getClass().getSimpleName());
    	
    	if (this.activeProfile != "production") {
    		// Sensitive information, only if not in production mode
    		
	    	err.setStackTrace(this.serializeStackTrace(throwable));
	    	
	    	if (throwable.getLocalizedMessage() != throwable.getMessage())
	    		err.setDebugMessage(throwable.getLocalizedMessage());
	    	
	    	if (throwable.getCause() != null)
	    		err.setInnerException(convertException(throwable.getCause()));
    	}
    	
    	return err;
    }
    
    protected String serializeStackTrace(final Throwable throwable) {
		var sb = new StringBuilder();
		var counter = 0;
		
		for (StackTraceElement el : throwable.getStackTrace()) {
			sb.append(el.toString() + "\n   ");
			
			if (!el.getClassName().startsWith("ch.uzh.marugoto"))
				counter++;
			
			// Break after 3 stack trace items not of this project to avoid bloated stack trace
			if (counter == 3)
				break;
		}
		
		return sb.toString();
    }
}
