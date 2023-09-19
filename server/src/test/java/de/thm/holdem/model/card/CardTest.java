package de.thm.holdem.model.card;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void Should_CorrectlyCompareTwoCards() {
        Card card1 = new Card(Rank.TWO, Suit.HEARTS);
        Card card2 = new Card(Rank.JACK, Suit.DIAMONDS);
        Card card3 = new Card(Rank.TWO, Suit.CLUBS);

        assertTrue(card1.compareTo(card2) < 0);
        assertTrue(card2.compareTo(card3) > 0);
        assertEquals(0, card1.compareTo(card3));
    }
}