package de.thm.holdem.model.card;

/**
 * Enum to represent the suits of a card.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public enum Suit{
    HEARTS("&#9829;", "red"),
    DIAMONDS("&#9670;", "red"),
    CLUBS("&#9827;", "black"),
    SPADES("&#9824;", "black"),
    ;
    private final String symbol;

    private final String color;

    Suit(String symbol, String color) {
        this.symbol = symbol;
        this.color = color;
    }
    public String getSymbol() { return symbol; }

    public String getColor() { return color; }
}