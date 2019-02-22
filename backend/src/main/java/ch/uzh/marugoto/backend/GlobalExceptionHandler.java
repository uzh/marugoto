package ch.uzh.marugoto.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.marugoto.backend.exception.RequestValidationException;
import ch.uzh.marugoto.backend.helper.ExceptionHelper;
import ch.uzh.marugoto.backend.resource.ApiError;
import ch.uzh.marugoto.core.data.Messages;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
	
	@Value("${spring.profiles.active}")
	private String activeProfile;
	@Autowired
	private Messages messages;

	/**
	 * Handler for validation exceptions
	 *
	 * @param e ValidationException
	 */
	@ExceptionHandler(value = RequestValidationException.class)
	public ResponseEntity<ApiError> handleException(RequestValidationException e) {
		var err = ExceptionHelper.convertException(activeProfile, e);
		err.setMessage(messages.get(err.getMessage()));
		err.setErrorList(e.getFieldErrors());

		ExceptionHelper.prepareError(activeProfile, err, e);

		return new ResponseEntity<ApiError>(err, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Global exceptions handler
	 *
	 * @param e
	 * @return
	 */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
    	var err = ExceptionHelper.convertException(activeProfile, e);
		ExceptionHelper.prepareError(activeProfile, err, e);

        return new ResponseEntity<ApiError>(err, HttpStatus.BAD_REQUEST);
    }
}
