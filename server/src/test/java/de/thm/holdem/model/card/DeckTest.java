package de.thm.holdem.model.card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    void Should_Contain52CardsInDeck() {
        assertEquals(52, deck.size());
    }

    @Test
    void Should_ShuffleTheDeck() {
        Deck originalDeck = new Deck();
        List<Card> originalCards = new ArrayList<>(originalDeck.getDeck());

        deck.shuffle();

        assertNotEquals(originalCards, deck.getDeck());
    }

    @Test
    public void Should_DrawCardFromDeck() {
        Card drawnCard = deck.drawCard();
        assertNotNull(drawnCard);

        assertEquals(51, deck.size());
    }

    @Test
    public void Should_BurnCardFromDeck() {
        deck.burnCard();

        assertEquals(51, deck.size());
    }

}