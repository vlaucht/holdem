package de.thm.holdem.model.card;

/**
 * Class to represent a card.
 * A card consists of a rank and a suit.
 *
 * @param rank the rank of the card
 * @param suit the suit of the card
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public record Card(Rank rank, Suit suit) implements Comparable<Card> {

    @Override
    public int compareTo(Card card) {
        return Integer.compare(this.rank.getNumericValue(), card.rank.getNumericValue());
    }
}
