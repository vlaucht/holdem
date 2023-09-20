package de.thm.holdem.exception;

/**
 * Exception that is thrown when a player tries to perform an illegal action.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public class IllegalGameActionException extends Exception {

    public IllegalGameActionException(String message) {
        super(message);
    }
}
