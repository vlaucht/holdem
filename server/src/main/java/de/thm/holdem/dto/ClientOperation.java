package de.thm.holdem.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum do define the operation that should be performed on the client side.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public enum ClientOperation {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    NONE("NONE"),

    JOIN_PLAYER("JOIN"),
    LEAVE_PLAYER("LEAVE"),

    START_GAME("START"),

    DEAL("DEAL"),

    PLAYER_ACTION("PLAYER_ACTION"),

    ROUND_CHANGE("ROUND_CHANGE"),

    SHOWDOWN("SHOWDOWN"),

    PLAYER_WINS("PLAYER_WINS"),

    ;

    /** The operation that should be performed on the client side. */
    private final String operation;

    ClientOperation(String operation) {
        this.operation = operation;
    }

    @JsonValue
    public String getOperation() {
        return operation;
    }

}