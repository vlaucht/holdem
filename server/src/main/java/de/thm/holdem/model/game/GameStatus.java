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

    /** A string representation of the game status */
    @JsonValue
    private final String prettyName;

    GameStatus(String prettyName) {
        this.prettyName = prettyName;
    }

    public String getPrettyName() {
        return this.prettyName;
    }
}
