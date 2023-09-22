package de.thm.holdem.model.game;

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
    private final String description;

    GameStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
