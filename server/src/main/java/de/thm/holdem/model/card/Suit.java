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

    /** The symbol that is shown on the card */
    private final String symbol;

    /** The color of the suit */
    private final String color;

    /**
     * Constructor to create a new suit.
     *
     * @param symbol the symbol of the suit.
     * @param color the color of the suit.
     */
    Suit(String symbol, String color) {
        this.symbol = symbol;
        this.color = color;
    }

    /**
     * Getter for the symbol.
     *
     * @return the symbol of the suit.
     */
    public String getSymbol() { return symbol; }

    /**
     * Getter for the color.
     *
     * @return the color of the suit.
     */
    public String getColor() { return color; }
}