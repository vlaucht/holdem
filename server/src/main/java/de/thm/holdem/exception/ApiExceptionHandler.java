package de.thm.holdem.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Handles and formats Exceptions and sends them to the client.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@RestControllerAdvice
public class ApiExceptionHandler {


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