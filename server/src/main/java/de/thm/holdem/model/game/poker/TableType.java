package de.thm.holdem.model.game.poker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.thm.holdem.exception.ApiRequestException;

/**
 * Table types for poker games.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public enum TableType {

    /** Fixed-Limit Texas Hold'em. Only 3 raises are allowed per betting round. */
    FIXED_LIMIT("Fixed-Limit", "FL"),

    /** No-Limit Texas Hold'em. Unlimited raises are allowed. */
    NO_LIMIT("No-Limit", "NL"),

    ;

    /** A name of the table type to be shown at the client. */
    private final String prettyName;

    /** A string representation of the table type for requests. */
    private final String stringRepresentation;


    /**
     * Constructor to create a table type.
     *
     * @param name the name of the table type to be shown to the client.
     * @param stringRepresentation the string representation of the table type used for requests.
     */
    TableType(String name, String stringRepresentation) {
        this.prettyName = name;
        this.stringRepresentation = stringRepresentation;
    }

    /**
     * Returns the name of the table type. This value is used in
     * JSON serialization.
     *
     * @return the name of the table type
     */
    @JsonValue
    public String getPrettyName() {
        return this.prettyName;
    }


    /**
     * Finds the table type for the given string representation.
     * This method is used in JSON deserialization.
     *
     * @param value the string representation of the requested table type
     * @return the table type if found
     * @throws IllegalArgumentException if the given string representation could not be mapped to a table type
     */
    @JsonCreator
    public static TableType fromValue(String value) {
        if (value.equalsIgnoreCase(FIXED_LIMIT.stringRepresentation)) return FIXED_LIMIT;
        if (value.equalsIgnoreCase(NO_LIMIT.stringRepresentation)) return NO_LIMIT;
        throw new ApiRequestException("Invalid TableType: " + value);
    }

}