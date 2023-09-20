package de.thm.holdem.exception;

/**
 * Exception that is thrown when an action was not successful.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public class GameActionException extends Exception {

    public GameActionException(String message) {
        super(message);
    }
}
