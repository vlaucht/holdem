package de.thm.holdem.model.game.poker;

import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Rank;
import de.thm.holdem.model.card.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PokerHandTest {

    private PokerHand pokerHand;

    @BeforeEach
    void setUp() {
        pokerHand = new PokerHand();
    }

    @Test
    void Should_AddCardToHand() {
        Card card = new Card(Rank.ACE, Suit.HEARTS);
        pokerHand.addCard(card);
        assertEquals(1, pokerHand.getCards().size());
        assertEquals(card, pokerHand.getCards().get(0));
    }

    @Test
    void Should_AddMultipleCardsToHand() {
        List<Card> cardsToAdd = Arrays.asList(
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.KING, Suit.SPADES),
                new Card(Rank.QUEEN, Suit.DIAMONDS)
        );
        pokerHand.addCards(cardsToAdd);
        assertEquals(cardsToAdd.size(), pokerHand.getCards().size());
        assertTrue(pokerHand.getCards().containsAll(cardsToAdd));
    }

    @Test
    void Should_ResetHand() {
        Card card = new Card(Rank.ACE, Suit.HEARTS);
        pokerHand.addCard(card);
        assertNotNull(pokerHand.getHandResult());
        pokerHand.reset();
        assertEquals(0, pokerHand.getCards().size());
        assertNull(pokerHand.getHandResult());
    }

    @Test
    void Should_EvaluateHandWithHighCard() {
        Card highCard = new Card(Rank.ACE, Suit.HEARTS);
        List<Card> cards = Arrays.asList(
                highCard,
                new Card(Rank.THREE, Suit.SPADES),
                new Card(Rank.QUEEN, Suit.DIAMONDS),
                new Card(Rank.SIX, Suit.CLUBS),
                new Card(Rank.TEN, Suit.HEARTS)
        );
        pokerHand.addCards(cards);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.HIGH_CARD, result.getHandType());
        assertEquals(List.of(highCard), result.getHandCards());
    }

    @Test
    void Should_EvaluateFourOfAKind() {
        List<Card> fourOfAKindCards = Arrays.asList(
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.ACE, Suit.CLUBS)
        );
        List<Card> cardsToEvaluate = new ArrayList<>(List.of(
                new Card(Rank.KING, Suit.HEARTS)
        ));
        cardsToEvaluate.addAll(fourOfAKindCards);
        pokerHand.addCards(cardsToEvaluate);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.FOUR_OF_A_KIND, result.getHandType());
        assertEquals(4, result.getHandCards().size());
        assertEquals(fourOfAKindCards, result.getHandCards());
    }

    @Test
    void Should_EvaluateFullHouse() {
        List<Card> cards = Arrays.asList(
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.CLUBS),
                new Card(Rank.SEVEN, Suit.HEARTS)
        );

        pokerHand.addCards(cards);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.FULL_HOUSE, result.getHandType());
        assertEquals(5, result.getHandCards().size());
    }

    @Test
    void Should_EvaluateFlush() {
        List<Card> cards = Arrays.asList(
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.TWO, Suit.HEARTS)
        );

        pokerHand.addCards(cards);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.FLUSH, result.getHandType());
        assertEquals(5, result.getHandCards().size());
    }

    @Test
    void Should_EvaluateStraight() {
        List<Card> cards = Arrays.asList(
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.SPADES),
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.SIX, Suit.CLUBS),
                new Card(Rank.FIVE, Suit.HEARTS)
        );

        pokerHand.addCards(cards);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.STRAIGHT, result.getHandType());
        assertEquals(5, result.getHandCards().size());
    }

    @Test
    void Should_EvaluateStraightFlush() {
        List<Card> cards = Arrays.asList(
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.HEARTS),
                new Card(Rank.SIX, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.HEARTS)
        );

        pokerHand.addCards(cards);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.STRAIGHT_FLUSH, result.getHandType());
        assertEquals(5, result.getHandCards().size());
    }

    @Test
    void Should_EvaluateRoyalFlush() {
        List<Card> cards = Arrays.asList(
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.JACK, Suit.HEARTS),
                new Card(Rank.TEN, Suit.HEARTS)
        );

        pokerHand.addCards(cards);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.ROYAL_FLUSH, result.getHandType());
        assertEquals(5, result.getHandCards().size());
    }

    @Test
    void Should_EvaluateThreeOfAKind() {
        List<Card> threeOfAKindCards = Arrays.asList(
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.NINE, Suit.DIAMONDS)
        );
        List<Card> cardsToEvaluate = new ArrayList<>(Arrays.asList(
                new Card(Rank.SEVEN, Suit.CLUBS),
                new Card(Rank.TWO, Suit.HEARTS)
        ));


        cardsToEvaluate.addAll(threeOfAKindCards);
        pokerHand.addCards(cardsToEvaluate);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.THREE_OF_A_KIND, result.getHandType());
        assertEquals(3, result.getHandCards().size());
        assertEquals(threeOfAKindCards, result.getHandCards());
    }

    @Test
    void Should_EvaluateOnePair() {
        List<Card> onePairCards = Arrays.asList(
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.NINE, Suit.SPADES)
        );
        List<Card> cardsToEvaluate = new ArrayList<>(Arrays.asList(
                new Card(Rank.SEVEN, Suit.CLUBS),
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.DIAMONDS)
        ));
        cardsToEvaluate.addAll(onePairCards);

        pokerHand.addCards(cardsToEvaluate);
        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.ONE_PAIR, result.getHandType());
        assertEquals(2, result.getHandCards().size());
        assertEquals(onePairCards, result.getHandCards());
    }

    @Test
    void Should_EvaluateTwoPairs() {
        List<Card> twoPairsCards = Arrays.asList(
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES)
        );
        List<Card> cardsToEvaluate = new ArrayList<>(Arrays.asList(
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.DIAMONDS)
        ));
        cardsToEvaluate.addAll(twoPairsCards);

        pokerHand.addCards(cardsToEvaluate);
        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.TWO_PAIRS, result.getHandType());
        assertEquals(4, result.getHandCards().size());
        assertEquals(twoPairsCards, result.getHandCards());
    }

    @Test
    void Should_EvaluateHandWithLessThanFiveCards() {
        List<Card> cards = Arrays.asList(
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.QUEEN, Suit.DIAMONDS)
        );
        pokerHand.addCards(cards);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.ONE_PAIR, result.getHandType());
        assertEquals(2, result.getHandCards().size());
    }

    @Test
    void Should_FindAllFiveCardCombinationsOutOfSixCards() {
        List<Card> cards = new ArrayList<>(Arrays.asList(
                new Card(Rank.QUEEN, Suit.SPADES),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.QUEEN, Suit.DIAMONDS),
                new Card(Rank.QUEEN, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.HEARTS)
        ));
        pokerHand.addCards(cards);

        ArrayList<ArrayList<Card>> combinations = pokerHand.find5CardsCombinations(new ArrayList<>(cards));
        assertEquals(6, combinations.size());
    }

    @Test
    void Should_FindAllFiveCardCombinationsOutOfSevenCards() {
        List<Card> cards = new ArrayList<>(Arrays.asList(
                new Card(Rank.QUEEN, Suit.SPADES),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.QUEEN, Suit.DIAMONDS),
                new Card(Rank.QUEEN, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.TWO, Suit.HEARTS)
        ));
        pokerHand.addCards(cards);

        ArrayList<ArrayList<Card>> combinations = pokerHand.find5CardsCombinations(new ArrayList<>(cards));
        assertEquals(21, combinations.size());
    }

    @Test
    void Should_EvaluateHandWithSixCards() {
        List<Card> cards = Arrays.asList(
                new Card(Rank.QUEEN, Suit.SPADES),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.QUEEN, Suit.DIAMONDS),
                new Card(Rank.QUEEN, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.HEARTS)
        );
        pokerHand.addCards(cards);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.FOUR_OF_A_KIND, result.getHandType());
        assertEquals(4, result.getHandCards().size());
    }

    @Test
    void Should_EvaluateHandWithSevenCards() {
        List<Card> cards = Arrays.asList(
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.NINE, Suit.CLUBS),
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.TWO, Suit.HEARTS)
        );
        pokerHand.addCards(cards);

        PokerHandResult result = pokerHand.getHandResult();
        assertEquals(PokerHandType.TWO_PAIRS, result.getHandType());
        assertEquals(4, result.getHandCards().size());
    }


}