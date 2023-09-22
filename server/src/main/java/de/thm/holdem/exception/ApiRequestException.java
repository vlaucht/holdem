package de.thm.holdem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Used to throw an Api Exception that will be sent to the client.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Getter
public class ApiRequestException extends RuntimeException {

    private final HttpStatus httpStatus;

    /**
     * Creates the default ApiRequestException. HttpStatus 400: Bad Request will be
     * assigned.
     * @param message the message that is sent to the client.
     */
    public ApiRequestException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    /**
     * Creates a custom ApiRequestException.
     * @param message the message that is sent to the client.
     * @param httpStatus the HttpStatus that is sent to the client.
     */
    public ApiRequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}