package de.thm.holdem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles and formats Exceptions and sends them to the client.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * An exception handler if validation fails.
     *
     * <p>This method is called if a {@link MethodArgumentNotValidException} is thrown. This happens
     * if a request body is invalid. All binding errors are extracted and formatted and an
     * ApiException is created.
     *
     * @param exception the exception
     * @return a response entity with the ApiException and the HTTP status code
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationFailed(MethodArgumentNotValidException exception) {
        Map<String, String> map = new HashMap<>();
        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> map.put(fieldError.getField(), fieldError.getDefaultMessage()));
        ApiError apiError =
                new ApiError(
                        Timestamp.from(Instant.now()),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        map.values().toString());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<Object> handleNotAuthorizedException(NotAuthorizedException e) {

        ApiError apiError = new ApiError(
                Timestamp.from(Instant.now()),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {

        ApiError apiError = new ApiError(
                Timestamp.from(Instant.now()),
                e.getHttpStatus().value(),
                e.getHttpStatus().getReasonPhrase(),
                e.getMessage());
        return new ResponseEntity<>(apiError, e.getHttpStatus());
    }

    /**
     * An exception handler if a user is not found.
     *
     * @param exception the exception
     * @return a response entity with the ApiException and the HTTP status code
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(UserNotFoundException exception) {
        ApiError apiError =
                new ApiError(
                        Timestamp.from(Instant.now()),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        exception.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

}