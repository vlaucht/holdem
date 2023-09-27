package de.thm.holdem.model.game.poker;

/**
 * Enum to represent the actions a player can take.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public enum PokerPlayerAction {
    FOLD("fold"),
    CALL("call"),
    RAISE("raise"),
    CHECK("check"),
    ALL_IN("allIn"),
    SMALL_BLIND("smallBlind"),
    BIG_BLIND("bigBlind"),
    ;

    /** The string value of the action */
    private final String stringValue;

    /**
     * Constructor to create a new action.
     *
     * @param stringValue the string value of the action.
     */
    PokerPlayerAction(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * Getter for the string value.
     *
     * @return the string value of the action.
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Method to get the action from a string.
     *
     * @param action the string value of the action.
     * @return the action if it exists, else null.
     */
    public static PokerPlayerAction fromString(String action) {
        for (PokerPlayerAction value : PokerPlayerAction.values()) {
            if (value.stringValue.equalsIgnoreCase(action)) {
                return value;
            }
        }
        return null;
    }

}
