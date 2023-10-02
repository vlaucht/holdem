package de.thm.holdem.model.game.poker;

/**
 * Enum to represent the type of a poker hand.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public enum PokerHandType {
    HIGH_CARD(0, "High Card"),
    ONE_PAIR(1000000, "One Pair"),
    TWO_PAIRS(2000000, "Two Pairs"),
    THREE_OF_A_KIND(3000000, "Three of a Kind"),
    STRAIGHT(4000000, "a Straight"),
    FLUSH(5000000, "a Flush"),
    FULL_HOUSE(6000000, "a Full House"),
    FOUR_OF_A_KIND(7000000, "Four of a Kind"),
    STRAIGHT_FLUSH(8000000, "a Straight Flush"),
    ROYAL_FLUSH(9000000, "a Royal Flush"),

    ;

    /** The base value of the hand type. The value is chosen in a way that a higher hand type always has the higher value,
     * regardless of the specific cards*/
    private final int baseValue;

    /** A string representation of the hand type to be shown to the client. */
    private final String prettyName;

    PokerHandType(int baseValue, String prettyName) {
        this.baseValue = baseValue;
        this.prettyName = prettyName;
    }

    /**
     * Returns the base value of the hand type.
     *
     * @return the base value of the hand type.
     */
    public int getBaseValue() {
        return baseValue;
    }

    /**
     * Returns the pretty name of the hand type to be shown to the client.
     *
     * @return the pretty name of the hand type to be shown to the client.
     */
    public String getPrettyName() {
        return prettyName;
    }
}
