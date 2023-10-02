package de.thm.holdem.model.game.poker;

import de.thm.holdem.model.card.Card;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Class to calculate and store the result of a poker hand.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public class PokerHandResult implements Comparable<PokerHandResult> {

    /** The type of the evaluated hand. */
    @Getter
    private final PokerHandType handType;

    /** The value of the hand. */
    @Getter
    private final int handValue;

    /** A list of up to 5 cards that are used to calculate the hand (e.g. 2 hole cards + 3 community cards). */
    private final List<Card> calculationCards;

    /** A list of cards containing the cards that make up the hand (e.g. the 4 cards that form a Four-Of-A-Kind). */
    @Getter
    private final List<Card> handCards;

    /**
     * Constructor to create a new PokerHandResult.
     *
     * @param handType the type of the hand.
     * @param calculationCards the up to 5 cards that are used to calculate the hand (e.g. 2 hole cards + 3 community cards).
     * @param handCards the cards that make up the hand (e.g. the 4 cards that form a Four-Of-A-Kind).
     */
    public PokerHandResult(PokerHandType handType, List<Card> calculationCards, List<Card> handCards) {
        this.handType = handType;
        this.calculationCards = calculationCards;
        this.handCards = handCards;
        this.handCards.sort(Collections.reverseOrder());
        this.calculationCards.sort(Collections.reverseOrder());
        this.handValue = calculateHandValue();
    }

    /**
     * Method to calculate the value of the hand.
     *
     * @return the value of the hand.
     */
    private int calculateHandValue() {
        return switch (handType) {
            case HIGH_CARD -> calculateHighCardValue();
            case ONE_PAIR -> calculateOnePairValue();
            case TWO_PAIRS -> calculateTwoPairsValue();
            case THREE_OF_A_KIND -> calculateThreeOfAKindValue();
            // for the following hand types, the value is the base value + the value of the highest card
            case STRAIGHT, FOUR_OF_A_KIND, FLUSH, STRAIGHT_FLUSH -> handType.getBaseValue() + calculationCards.get(0).rank().getValue();
            case FULL_HOUSE -> calculateFullHouseValue();
            case ROYAL_FLUSH -> handType.getBaseValue();
            default -> 0;
        };
    }

    /**
     * Method to calculate the value of TWO_PAIRS
     *
     * @return TWO_PAIRS (2000000) + 14^2 * HighPairCard + 14* LowPairCard + UnmatchedCard
     */
    private int calculateTwoPairsValue() {
        int value = handType.getBaseValue() + (int) (Math.pow(14, 2) * handCards.get(0).rank().getValue());
        value += (14 * handCards.get(2).rank().getValue());
        List<Card> filteredCards = calculationCards.stream().filter(card -> !handCards.contains(card)).toList();
        if (filteredCards.size() == 1) {
            value += filteredCards.get(0).rank().getValue();
        }
        return value;
    }

    /**
     * Method to calculate the value of ONE_PAIR
     *
     * @return ONE_PAIR (1000000) + 14^3 * PairCard + 14^2* HighestCard + 14* MiddleCard + LowestCard
     */
    private int calculateOnePairValue() {
        int value = handType.getBaseValue() + (int) (Math.pow(14, 3) * handCards.get(0).rank().getValue());
        List<Card> filteredCards = calculationCards.stream().filter(card -> !handCards.contains(card)).toList();
        for (int i = 0; i < filteredCards.size(); i++) {
            value += filteredCards.get(i).rank().getValue() * Math.pow(14, 2 - i);
        }
        return value;
    }

    /**
     * Method to calculate the value of a THREE_OF_A_KIND
     *
     * @return THREE_OF_A_KIND (3000000) + IF xxxyz -> 3* value(x) + order(y) + order(z) ELSE 3* order(z) + order(y) + order(x)
     */
    private int calculateThreeOfAKindValue() {
        int value = handType.getBaseValue() + 3* handCards.get(0).rank().getValue();
        for (Card card : calculationCards) {
            if (!handCards.contains(card)) { // Avoid counting the three of a kind cards again
                value += card.rank().getValue();
            }
        }
        return value;
    }

    /**
     * Method to calculate the value of a FULL_HOUSE
     *
     * @return FULL_HOUSE (6000000) + IF xxyyy -> 2* value(x) + 3* value(y) ELSE 3* value(x) + 2* value(y)
     */
    private int calculateFullHouseValue() {
        return handType.getBaseValue() + (calculationCards.get(0).rank().getValue() == calculationCards.get(2).rank().getValue()
                ? (3 * calculationCards.get(0).rank().getValue() + 2 * calculationCards.get(2).rank().getValue())
                : (2 * calculationCards.get(0).rank().getValue() + 3 * calculationCards.get(2).rank().getValue()));
    }

    /**
     * Method to calculate the value of HIGH_CARD.
     *
     * @return 14^4* highestCard + 14^3* 2ndHighestCard + 14^2* 3rdHighestCard + 14* 4thHighestCard + LowestCard.
     */
    private int calculateHighCardValue() {
        int value = 0;
        for (int i = 0; i < calculationCards.size(); i++) {
            value += calculationCards.get(i).rank().getValue() * Math.pow(14, 4 - i);
        }
        return value;
    }

    /**
     * Method to compare two PokerHandResults.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this hand is less than, equal to, or greater than the specified hand.
     */
    @Override
    public int compareTo(PokerHandResult o) {
        return Integer.compare(this.handValue, o.handValue);
    }
}
