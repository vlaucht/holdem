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

    private final String value;
    private final int numericValue;

    Rank(String value, int order) {
        this.value = value;
        this.numericValue = order;
    }

    public String getValue() {
        return value;
    }

    public int getNumericValue() {
        return numericValue;
    }
}