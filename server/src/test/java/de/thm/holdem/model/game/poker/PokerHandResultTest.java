package de.thm.holdem.model.game.poker;

import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Rank;
import de.thm.holdem.model.card.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PokerHandResultTest {

    private PokerHandResult pokerHandResult;
    @BeforeEach
    void setUp() {
        Card c1 = new Card(Rank.FIVE, Suit.DIAMONDS);
        Card c2 = new Card(Rank.FIVE, Suit.HEARTS);
        Card c3 = new Card(Rank.FIVE, Suit.CLUBS);
        Card c4 = new Card(Rank.TWO, Suit.CLUBS);
        Card c5 = new Card(Rank.TWO, Suit.HEARTS);

        List<Card> cards = List.of(c1, c2, c3, c4, c5);

        pokerHandResult = new PokerHandResult(PokerHandType.FULL_HOUSE, new ArrayList<>(cards), new ArrayList<>(cards));
    }


    /**
     * Hand1: A A A 8 8
     * Hand2: A A A 5 5
     * Hand1 should be stronger, even though both are a full house
     */
    @Test
    void Should_ValueStrongerFullHouseHigher() {
        int fullHouse5High = pokerHandResult.getHandValue();
        List<Card> cards = List.of(new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.TWO, Suit.CLUBS),
                new Card(Rank.TWO, Suit.CLUBS),
                new Card(Rank.TWO, Suit.HEARTS));
        pokerHandResult = new PokerHandResult(PokerHandType.FULL_HOUSE, new ArrayList<>(cards), new ArrayList<>(cards));
        int fullHouse2High = pokerHandResult.getHandValue();

        assertTrue(fullHouse5High > fullHouse2High);
    }

    @Test
    void Should_CalculateXXXYYFullHouse() {
        assertEquals(6000000 + 14 * 3 * 5 + 2 * 2, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateXXYYYFullHouse() {
        List<Card> cards = List.of(
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.CLUBS),
                new Card(Rank.FIVE, Suit.SPADES),
                new Card(Rank.FIVE, Suit.HEARTS));
        pokerHandResult = new PokerHandResult(PokerHandType.FULL_HOUSE, new ArrayList<>(cards), new ArrayList<>(cards));

        assertEquals(6000000 + 14 * 3 * 5 + 2 * 14, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateHighCard() {
        List<Card> cards = List.of(
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.FIVE, Suit.HEARTS));
        pokerHandResult = new PokerHandResult(PokerHandType.HIGH_CARD, new ArrayList<>(cards), new ArrayList<>(cards));
        int expected = (int) (Math.pow(14, 4) * 14 + Math.pow(14, 3) * 12 + Math.pow(14, 2) * 10 + 14 * 9 + 5);
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateOnePair() {
        List<Card> pair = List.of(
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.ACE, Suit.HEARTS));
        List<Card> cards = new ArrayList<>(List.of(
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.FIVE, Suit.HEARTS)));
        cards.addAll(pair);
        pokerHandResult = new PokerHandResult(PokerHandType.ONE_PAIR, new ArrayList<>(cards), new ArrayList<>(pair));
        int expected = 1000000 + (int) (Math.pow(14, 3) * 14 + Math.pow(14, 2) * 10 + 14 * 9 + 5);
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateOnePairWithLessThanFiveCards() {
        List<Card> pair = List.of(
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.ACE, Suit.HEARTS));
        List<Card> cards = new ArrayList<>(List.of(
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.NINE, Suit.SPADES)));
        cards.addAll(pair);
        pokerHandResult = new PokerHandResult(PokerHandType.ONE_PAIR, new ArrayList<>(cards), new ArrayList<>(pair));
        int expected = 1000000 + (int) (Math.pow(14, 3) * 14 + Math.pow(14, 2) * 10 + 14 * 9);
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateTwoPairs() {
        List<Card> pair1 = new ArrayList<>(List.of(
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.ACE, Suit.HEARTS)));
        List<Card> pair2 = List.of(
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.TEN, Suit.SPADES));
        List<Card> cards = new ArrayList<>(List.of(
                new Card(Rank.NINE, Suit.SPADES)));
        cards.addAll(pair1);
        cards.addAll(pair2);
        pair1.addAll(pair2);
        pokerHandResult = new PokerHandResult(PokerHandType.TWO_PAIRS, new ArrayList<>(cards), new ArrayList<>(pair1));
        int expected = 2000000 + (int) (Math.pow(14, 2) * 14 + 14 * 10 + 9);
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateThreeOfAKind_If_XXXYZ() {
        List<Card> threeOfAKind = List.of(
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.ACE, Suit.CLUBS));
        List<Card> cards = new ArrayList<>(List.of(
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.NINE, Suit.SPADES)));
        cards.addAll(threeOfAKind);
        pokerHandResult = new PokerHandResult(PokerHandType.THREE_OF_A_KIND, new ArrayList<>(cards), new ArrayList<>(threeOfAKind));
        int expected = (int) (3000000 + Math.pow(14, 2) * 14 + 14 * 10 + 9);
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateThreeOfAKind_If_YXXXZ() {
        List<Card> threeOfAKind = List.of(
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.CLUBS));
        List<Card> cards = new ArrayList<>(List.of(
                new Card(Rank.TEN, Suit.CLUBS)));
        cards.addAll(threeOfAKind);
        cards.add(new Card(Rank.FIVE, Suit.SPADES));
        pokerHandResult = new PokerHandResult(PokerHandType.THREE_OF_A_KIND, new ArrayList<>(cards), new ArrayList<>(threeOfAKind));
        int expected = (int) (3000000 + Math.pow(14, 2) * 7 + 14 * 10 + 5);
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateThreeOfAKind_If_YZXXX() {
        List<Card> threeOfAKind = List.of(
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.CLUBS));
        List<Card> cards = new ArrayList<>(List.of(
                new Card(Rank.ACE, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.SPADES)));
        cards.addAll(threeOfAKind);
        pokerHandResult = new PokerHandResult(PokerHandType.THREE_OF_A_KIND, new ArrayList<>(cards), new ArrayList<>(threeOfAKind));
        int expected = (int) (3000000 + Math.pow(14, 2) * 7 + 14 * 14 + 12);
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateStraight() {
        List<Card> cards = List.of(
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.CLUBS),
                new Card(Rank.JACK, Suit.SPADES),
                new Card(Rank.TEN, Suit.HEARTS));
        pokerHandResult = new PokerHandResult(PokerHandType.STRAIGHT, new ArrayList<>(cards), new ArrayList<>(cards));
        assertEquals(4000000 + 14, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateFourOfAKind_If_XXXXY() {
        List<Card> fourOfAKind = List.of(
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.ACE, Suit.CLUBS),
                new Card(Rank.ACE, Suit.SPADES));
        List<Card> cards = new ArrayList<>(List.of(
                new Card(Rank.TEN, Suit.CLUBS)));
        cards.addAll(fourOfAKind);
        pokerHandResult = new PokerHandResult(PokerHandType.FOUR_OF_A_KIND, new ArrayList<>(cards), new ArrayList<>(fourOfAKind));
        int expected = 7000000 + 14 * 14 + 10;
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateFourOfAKind_If_YXXXX() {
        List<Card> fourOfAKind = List.of(
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.CLUBS),
                new Card(Rank.SEVEN, Suit.SPADES));
        List<Card> cards = new ArrayList<>(List.of(
                new Card(Rank.ACE, Suit.CLUBS)));
        cards.addAll(fourOfAKind);
        pokerHandResult = new PokerHandResult(PokerHandType.FOUR_OF_A_KIND, new ArrayList<>(cards), new ArrayList<>(fourOfAKind));
        int expected = 7000000 + 14 * 7 + 14;
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateFlush() {
        List<Card> cards = List.of(
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.HEARTS));
        pokerHandResult = new PokerHandResult(PokerHandType.FLUSH, new ArrayList<>(cards), new ArrayList<>(cards));
        int expected = 5000000 + (int) (Math.pow(14, 4) * 14 + Math.pow(14, 3) * 12 + Math.pow(14, 2) * 10 + 14 * 9 + 5);
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateStraightFlush() {
        List<Card> cards = List.of(
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.SIX, Suit.HEARTS));
        pokerHandResult = new PokerHandResult(PokerHandType.STRAIGHT_FLUSH, new ArrayList<>(cards), new ArrayList<>(cards));
        int expected = 8000000 + 10;
        assertEquals(expected, pokerHandResult.getHandValue());
    }

    @Test
    void Should_CalculateRoyalFlush() {
        List<Card> cards = List.of(
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.JACK, Suit.HEARTS),
                new Card(Rank.TEN, Suit.HEARTS));
        pokerHandResult = new PokerHandResult(PokerHandType.ROYAL_FLUSH, new ArrayList<>(cards), new ArrayList<>(cards));
        assertEquals(9000000, pokerHandResult.getHandValue());
    }


}