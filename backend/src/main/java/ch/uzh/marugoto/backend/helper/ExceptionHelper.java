package ch.uzh.marugoto.backend.helper;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.uzh.marugoto.backend.resource.ApiError;

public class ExceptionHelper {

    public static ApiError convertException(String activeProfile, final Throwable throwable) {
        var err = new ApiError();
        err.setMessage(throwable.getMessage());
        err.setException(throwable.getClass().getSimpleName());

        if (activeProfile != "production") {
            // Sensitive information, only if not in production mode
            err.setStackTrace(serializeStackTrace(throwable));

            if (throwable.getLocalizedMessage() != throwable.getMessage()) {
                err.setDebugMessage(throwable.getLocalizedMessage());
            }

            if (throwable.getCause() != null) {
                // recursion
                err.setInnerException(convertException(activeProfile, throwable.getCause()));
            }
        }

        return err;
    }

    public static void prepareError(String activeProfile, ApiError err, Exception e) {
        // Add requested controller path
        var uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        if (uriBuilder != null) {
            var uriComponents = uriBuilder.build();
            if (uriComponents != null)
                err.setPath(uriComponents.getPath());
        }

        if (activeProfile != "production") {
            // Sensitive information, only if not in production mode
            // Add filename and line number
            if (e.getStackTrace() != null && e.getStackTrace().length > 0)
                err.setFile(e.getStackTrace()[0].getFileName() + ":" + e.getStackTrace()[0].getLineNumber());
        }
    }

    private static String serializeStackTrace(final Throwable throwable) {
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
