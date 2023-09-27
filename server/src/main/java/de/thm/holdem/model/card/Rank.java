package de.thm.holdem.model.card;

/**
 * Enum to represent the ranks of a card.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public enum Rank {
    TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5),
    SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9),
    TEN("10", 10), JACK("J", 11), QUEEN("Q", 12), KING("K", 13),
    ACE("A", 14);

    /** The symbol that is shown on the card */
    private final String symbol;

    /** The value of the rank for comparison and calculations */
    private final int value;

    /**
     * Constructor to create a new rank.
     *
     * @param symbol the symbol of the rank.
     * @param value the value of the rank.
     */
    Rank(String symbol, int value) {
        this.symbol = symbol;
        this.value = value;
    }

    /**
     * Getter for the symbol.
     *
     * @return the symbol of the rank.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Getter for the value.
     *
     * @return the value of the rank.
     */
    public int getValue() {
        return value;
    }
}