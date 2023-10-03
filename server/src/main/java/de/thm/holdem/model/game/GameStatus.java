package de.thm.holdem.model.game;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for the status of a game.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public enum GameStatus {
    WAITING("Waiting for Players"),
    IN_PROGRESS("In Progress"),
    FINISHED("Finished"),

    ;

    /** A string representation of the game status to be shown to the client. */
    @JsonValue
    private final String prettyName;

    /**
     * Constructor to create a game status.
     *
     * @param prettyName the string representation of the game status to be shown to the client.
     */
    GameStatus(String prettyName) {
        this.prettyName = prettyName;
    }

    /**
     * Method to get the string representation of the game status to be shown to the client.
     *
     * @return the string representation of the game status.
     */
    public String getPrettyName() {
        return this.prettyName;
    }
}
