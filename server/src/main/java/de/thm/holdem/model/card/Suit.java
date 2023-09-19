package de.thm.holdem.model.card;

/**
 * Enum to represent the suits of a card.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public enum Suit{
    HEARTS("&#9829;"), DIAMONDS("&#9670;"), CLUBS("&#9827;"), SPADES("&#9824;");
    private final String value;
    Suit(String value) {
        this.value = value;
    }
    public String getValue() { return value; }
}