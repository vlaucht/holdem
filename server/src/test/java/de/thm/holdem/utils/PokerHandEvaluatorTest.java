package de.thm.holdem.utils;

import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Rank;
import de.thm.holdem.model.card.Suit;
import de.thm.holdem.utils.PokerHandEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PokerHandEvaluatorTest {
    private PokerHandEvaluator evaluator;
    @BeforeEach
    void setUp() {
        Card c1 = new Card(Rank.ACE, Suit.DIAMONDS);
        Card c2 = new Card(Rank.ACE, Suit.HEARTS);
        Card c3 = new Card(Rank.ACE, Suit.CLUBS);
        Card c4 = new Card(Rank.EIGHT, Suit.CLUBS);
        Card c5 = new Card(Rank.EIGHT, Suit.HEARTS);
        Card c6 = new Card(Rank.FOUR, Suit.SPADES);
        Card c7 = new Card(Rank.NINE, Suit.CLUBS);

        evaluator = new PokerHandEvaluator(c1, c2, c3, c4, c5, c6, c7);
    }

    @Test
    void Should_CreateAllCardCombinationsOnInitialization() {
        ArrayList<ArrayList<Card>> allCombs = evaluator.getAllCombs();
        assertEquals(21, allCombs.size());
    }

    /**
     * Tests if all 21 hands are different
     */
    @Test
    void Should_Generate21UniqueCombinations() {
        long uniques = evaluator.getAllCombs().stream().distinct().count();
        assertEquals(21, uniques);
    }

    /**
     * Hand: A A A 8 8 should equal 600000 (FULL_HOUSE) + 3* 14 (ACE) + 2* 8 (EIGHT) = 6000058
     */
    @Test
    void Should_FindAndEvaluateFullHouse() {
        assertEquals(6000058, evaluator.bestHand());
    }

    /**
     * Hand1: A A A 8 8
     * Hand2: A A A 5 5
     * Hand1 should be stronger, even though both are a full house
     */
    @Test
    void Should_ValueStrongerFullHouseHigher() {
        int fullHouse8High = evaluator.bestHand();
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.DIAMONDS), new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.FIVE, Suit.CLUBS), new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.FOUR, Suit.SPADES), new Card(Rank.NINE, Suit.CLUBS));
        int fullHouse5High = evaluator.bestHand();
        assertTrue(fullHouse8High > fullHouse5High);
    }

    /**
     * Two hands with same cards, but one has ACE high, and one has KING high and no poker hand matches
     * Hand with ACE high should win
     */
    @Test
    void Should_ValueHighestCard_If_NoHandMatches() {
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.JACK, Suit.SPADES),
                new Card(Rank.NINE, Suit.CLUBS), new Card(Rank.EIGHT, Suit.HEARTS), new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.THREE, Suit.DIAMONDS), new Card(Rank.THREE, Suit.HEARTS));
        int setAceHigh = evaluator.bestHand();
        evaluator = new PokerHandEvaluator(new Card(Rank.KING, Suit.CLUBS), new Card(Rank.JACK, Suit.SPADES),
                new Card(Rank.NINE, Suit.CLUBS), new Card(Rank.EIGHT, Suit.HEARTS), new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.THREE, Suit.DIAMONDS), new Card(Rank.THREE, Suit.HEARTS));
        int setKingHigh = evaluator.bestHand();
        assertTrue(setAceHigh > setKingHigh);
    }

    /**
     * One pair Test
     * Hand with A A 9 8 5
     * Should return 14^3 * 14 (Value of ACE) + 14^2 * 9 + 14 * 8 + 5 + 1000000 (Value of ONE_PAIR) = 1040297;
     */
    @Test
    void Should_EvaluateHandWithOnePair() {
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.NINE, Suit.CLUBS), new Card(Rank.EIGHT, Suit.HEARTS), new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.DIAMONDS), new Card(Rank.THREE, Suit.HEARTS));
        int onePairHand = evaluator.bestHand();
        assertEquals(1040297, onePairHand);
    }

    /**
     * Two Pair Test
     * Hand with A A 9 9 5
     * Should return 14^2 * 14(Value of ACE) + 14* 9 + 5 + 2000000 (Value of TWO_PAIR) = 2002875
     */
    @Test
    void Should_EvaluateHandWithTwoPairs() {
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.NINE, Suit.CLUBS), new Card(Rank.NINE, Suit.HEARTS), new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.DIAMONDS), new Card(Rank.THREE, Suit.HEARTS));
        int twoPairHand = evaluator.bestHand();
        assertEquals(2002875, twoPairHand);
    }

    /**
     * Three Of A Kind Test
     * Hand with A A A 9 5
     * Should return 3* 14 (Value of ACE) + 9 + 5 + 3000000 = 3000056
     */
    @Test
    void Should_EvaluateThreeOfAKind() {
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.ACE, Suit.DIAMONDS), new Card(Rank.NINE, Suit.HEARTS), new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.DIAMONDS), new Card(Rank.THREE, Suit.HEARTS));
        int threeOfAKindHand = evaluator.bestHand();
        assertEquals(3000056, threeOfAKindHand);
    }

    /**
     * Straight Test
     * Hand with A J 7 6 5 4 3 should return a Straight 7 6 5 4 3
     * Should return 3 + 14* 4 + 14^2 * 5 + 14^3 * 6 + 14^4 * 7 + 4000000 (Value of STRAIGHT) = 4286415
     */
    @Test
    void Should_EvaluateStraight() {
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.JACK, Suit.SPADES),
                new Card(Rank.SEVEN, Suit.DIAMONDS), new Card(Rank.SIX, Suit.HEARTS), new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.DIAMONDS), new Card(Rank.THREE, Suit.HEARTS));
        int straightHand = evaluator.bestHand();
        assertEquals(4286415, straightHand);
    }

    /**
     * Flush Test
     * Hand with A (Clubs) J (Clubs) 7 (Clubs) 6 (Clubs) 5 (Diamond) 4 (Heart) 3 (Clubs)
     * Should return a Flush with A J 7 6 3 of CLUBS even though it is also a Straight. But Flush is stronger.
     * Should return 14^4 * 14 (Value of ACE) + 14^3 * 11 (Value of JACK) + 14^2 * 7 + 14* 6 + 3 + 5000000 (Value of FLUSH) = 5569467
     */
    @Test
    void Should_EvaluateFlush() {
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.JACK, Suit.CLUBS),
                new Card(Rank.SEVEN, Suit.CLUBS), new Card(Rank.SIX, Suit.CLUBS), new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.HEARTS), new Card(Rank.THREE, Suit.CLUBS));
        int flushHand = evaluator.bestHand();
        assertEquals(5569467, flushHand);
    }

    /**
     * Straight flush Test
     * Hand with 10 (Club) 9 (Clusb) 8 (Club) 7 (Club) 6 (Club) 4(Heart) 3 (Spade)
     * Should return a Straight flush with 10 9 8 7 6 of CLUBS
     * Should return 14^4 * 10 + 14^3 * 9 + 14^2 * 8 + 14 * 7 + 6 + 8000000 (Value of STRAIGHT_FLUSH) = 8410528
     */
    @Test
    void Should_EvaluateStraightFlush() {
        evaluator = new PokerHandEvaluator(new Card(Rank.TEN, Suit.CLUBS), new Card(Rank.NINE, Suit.CLUBS),
                new Card(Rank.EIGHT, Suit.CLUBS), new Card(Rank.SEVEN, Suit.CLUBS), new Card(Rank.SIX, Suit.CLUBS),
                new Card(Rank.FOUR, Suit.HEARTS), new Card(Rank.THREE, Suit.SPADES));
        int straightFlushHand = evaluator.bestHand();
        assertEquals(8410528, straightFlushHand);
    }


    /**
     * Royal Flush Test
     * Hand with A (Clubs) K (Clubs) Q (Clubs) J (Clubs) 10 (Clubs) 4 (Heart) 3 (Spade)
     * Should return a ROYAL FLUSH with A K Q J 10 of CLUBS
     * Should return 90000000 since ROYAL FLUSH is unbeatable
     */
    @Test
    public void Should_EvaluateRoyalFlush() {
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.QUEEN, Suit.CLUBS),
                new Card(Rank.KING, Suit.CLUBS), new Card(Rank.JACK, Suit.CLUBS), new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.FOUR, Suit.HEARTS), new Card(Rank.THREE, Suit.SPADES));
        int royalFlushHand = evaluator.bestHand();
        assertEquals(90000000, royalFlushHand);
    }

    /**
     * For of a kind Test
     * Hand with K K K K 5
     * Should return 5 + 14 * 13 (Value of Kind) + 14^2 * 13 + 14^3 * 13 + 14^4 * 13 + 7000000 (Value of FOUR_OF_A_KIND) = 7537815
     */
    @Test
    public void Should_EvaluateFourOfAKind() {
        evaluator = new PokerHandEvaluator(new Card(Rank.KING, Suit.CLUBS), new Card(Rank.KING, Suit.SPADES),
                new Card(Rank.KING, Suit.HEARTS), new Card(Rank.KING, Suit.DIAMONDS), new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.DIAMONDS), new Card(Rank.THREE, Suit.HEARTS));
        int fourOfAKindHand = evaluator.bestHand();
        assertEquals(7537815, fourOfAKindHand);
    }

    /**
     * Straight Flush VS 4 Of A Kind Test
     * A Straight Flush should win against 4 Of A Kind, even if 4 Of A Kind has higher cards
     * Should return Straight flush > 4 Of A Kind
     */
    @Test
    public void Should_ValueStraightFlushHigherThan4ofAKind() {
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.ACE, Suit.HEARTS), new Card(Rank.ACE, Suit.DIAMONDS), new Card(Rank.FIVE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.DIAMONDS), new Card(Rank.SEVEN, Suit.HEARTS));
        int fourOfAKindHand = evaluator.bestHand();
        evaluator = new PokerHandEvaluator(new Card(Rank.TEN, Suit.HEARTS), new Card(Rank.NINE, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.CLUBS), new Card(Rank.SIX, Suit.CLUBS), new Card(Rank.FIVE, Suit.CLUBS),
                new Card(Rank.FOUR, Suit.CLUBS), new Card(Rank.THREE, Suit.CLUBS));
        int straightFlushHand = evaluator.bestHand();
        assertTrue(straightFlushHand > fourOfAKindHand);
    }

    /**
     * For two hands with the same Four Of A Kind
     * Hand1: K K K K 9
     * Hand2: K K K K Q
     * The Kicker Card shout determine the winner
     */
    @Test
    public void Should_DecideByKickerCard_If_HandsAreEqual() {
        evaluator = new PokerHandEvaluator(new Card(Rank.KING, Suit.CLUBS), new Card(Rank.KING, Suit.SPADES),
                new Card(Rank.KING, Suit.HEARTS), new Card(Rank.KING, Suit.DIAMONDS), new Card(Rank.NINE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.DIAMONDS), new Card(Rank.THREE, Suit.HEARTS));
        int handWithLowerKicker = evaluator.bestHand();
        evaluator = new PokerHandEvaluator(new Card(Rank.KING, Suit.CLUBS), new Card(Rank.KING, Suit.SPADES),
                new Card(Rank.KING, Suit.HEARTS), new Card(Rank.KING, Suit.DIAMONDS), new Card(Rank.QUEEN, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.DIAMONDS), new Card(Rank.THREE, Suit.HEARTS));
        int handWithHigherKicker = evaluator.bestHand();
        assertTrue(handWithHigherKicker > handWithLowerKicker);
    }

    /**
     * Tests if calculation works correctly, and a high card with a lot of low cards is stronger than
     * a card one lower than the high card and a lot of other high cards
     */
    @Test
    public void Should_ValueHighestCardAndIgnoreLowerCards() {
        evaluator = new PokerHandEvaluator(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.FIVE, Suit.SPADES),
                new Card(Rank.TWO, Suit.HEARTS), new Card(Rank.THREE, Suit.DIAMONDS), new Card(Rank.SIX, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.DIAMONDS), new Card(Rank.EIGHT, Suit.HEARTS));
        int handWithHighestCard = evaluator.bestHand();
        evaluator = new PokerHandEvaluator(new Card(Rank.KING, Suit.CLUBS), new Card(Rank.QUEEN, Suit.SPADES),
                new Card(Rank.JACK, Suit.HEARTS), new Card(Rank.TEN, Suit.DIAMONDS), new Card(Rank.EIGHT, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.DIAMONDS), new Card(Rank.SIX, Suit.HEARTS));
        int handWithLowerHighestCard = evaluator.bestHand();
        assertTrue(handWithHighestCard > handWithLowerHighestCard);
    }


}