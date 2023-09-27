package de.thm.holdem.model.card;

import java.util.Collections;

import java.util.*;

/**
 * Class to represent a deck of cards.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public class Deck {

    /**
     * A Stack used to store the collection of cards in the deck
     */
    private final Stack<Card> deck;

    /**
     * Constructor to create a new deck.
     * The deck is filled with all 52 card variations.
     */
    public Deck() {
        deck = new Stack<>();
        /* Stream to go through all 4 suits */
        Arrays.stream(Suit.values()).flatMap(suit ->
                        /* Stream to go through all 14 ranks */
                        Arrays.stream(Rank.values())
                                .map(rank -> new Card(rank, suit)))
                /* pushes for all 52 card variations a card to the stack */
                .forEach(deck::push);
    }

    /**
     * Method to shuffle the deck.
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }

    /**
     * Method to draw a card from the deck.
     *
     * @return the card that was drawn
     */
    public Card drawCard() {
        if (deck.isEmpty()) {
            throw new NoSuchElementException("The deck is empty.");
        }
        return deck.pop();
    }

    /**
     * Method to burn the first card of the deck.
     */
    public void burnCard() {
        if (deck.isEmpty()) {
            throw new NoSuchElementException("The deck is empty.");
        }
        deck.pop();
    }

    /**
     * Method to get all cards from the deck.
     *
     * @return the stack of cards
     */
    public Stack<Card> getDeck() { return deck; }

    /**
     * Method to get the size of the deck.
     *
     * @return the size of the deck
     */
    public int size() { return deck.size(); }
}
