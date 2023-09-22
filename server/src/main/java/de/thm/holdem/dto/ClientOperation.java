package de.thm.holdem.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClientOperation {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    NONE("NONE")

    ;

    private final String operation;

    ClientOperation(String operation) {
        this.operation = operation;
    }

    @JsonValue
    public String getOperation() {
        return operation;
    }

}