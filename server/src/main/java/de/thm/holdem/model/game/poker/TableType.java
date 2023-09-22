package de.thm.holdem.model.game.poker;

/**
 * Table types for poker games.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public enum TableType {

    /** Fixed-Limit Texas Hold'em. */
    FIXED_LIMIT("Fixed-Limit"),

    /** No-Limit Texas Hold'em. */
    NO_LIMIT("No-Limit"),

    ;

    /** A string representation of the table type. */
    private final String name;


    TableType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}