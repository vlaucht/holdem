package de.thm.holdem.service;

import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Suit;

import java.util.*;
import java.util.stream.Collectors;

public class PokerHandEvaluator {

    /**
     * Values for the different base variations of hands
     */
    private static final int ROYAL_FLUSH = 90000000, STRAIGHT_FLUSH = 8000000, FOUR_OF_A_KIND = 7000000,
            FULL_HOUSE = 6000000, FLUSH = 5000000, STRAIGHT = 4000000, SET = 3000000, TWO_PAIRS = 2000000,
            ONE_PAIR = 1000000;

    /**
     * ArrayList which will store all 21 5-Card-Combinations out of 7 cards
     */
    private final ArrayList<ArrayList<Card>> allCardCombinations;

    /**
     * Constructor for HandEvaluator.
     *
     * <p>
     *     Pass an array of the 7 cards to evaluate.
     * </p>
     */
    public PokerHandEvaluator(Card... cards) {
        if (cards.length != 7) {
            throw new IllegalArgumentException("Exactly 7 cards are required.");
        }
        ArrayList<Card> cardsList = new ArrayList<>(Arrays.asList(cards));
        allCardCombinations = find5CardsCombinations(cardsList);
    }

    /**
     * Getter for the list of all 21 Combinations.
     *
     * @return a list of all 21 5-Card-Combinations
     */
    public ArrayList<ArrayList<Card>> getAllCombs() { return this.allCardCombinations; }

    /**
     * Method to get the best of the 21 hand combinations
     *
     * @return the calculated int value of the best hand
     */
    public int bestHand() {
        return allCardCombinations.stream().mapToInt(this::checkValueHand).max().orElse(0);
    }


    /**
     * Method to calculate the value of a hands strength
     *
     * @param cardArrayList an arraylist of 5 cards
     * @return the int value of the hand strength
     */
    private int checkValueHand(ArrayList<Card> cardArrayList) {
        sortList(cardArrayList);
        if (isFlush(cardArrayList) && isStraight(cardArrayList) && getOrderValue(cardArrayList, 4) == 14) return ROYAL_FLUSH;
        else if (isFlush(cardArrayList) && isStraight(cardArrayList)) return valueStraightFlush(cardArrayList);
        else if (isFourOfAKind(cardArrayList)) return valueFourOfAKind(cardArrayList);
        else if (isFullHouse(cardArrayList)) return valueFullHouse(cardArrayList);
        else if (isFlush(cardArrayList)) return valueFlush(cardArrayList);
        else if (isStraight(cardArrayList)) return valueStraight(cardArrayList);
        else if (isThreeOfAKind(cardArrayList)) return valueSet(cardArrayList);
        else if (isTwoPairs(cardArrayList)) return valueTwoPairs(cardArrayList);
        else if (isOnePair(cardArrayList)) return valueOnePair(cardArrayList);
        else return valueHighCard(cardArrayList);
    }

    /***
     * Method generates all possible combinations of 5 cards out of 7 cards
     *
     * @param cards an arraylist of 7 cards
     * @return all the possible 5 card combinations
     */
    private ArrayList<ArrayList<Card>> find5CardsCombinations(ArrayList<Card> cards) {
        ArrayList<ArrayList<Card>> allCardCombinations = new ArrayList<>();
        backtrack(allCardCombinations, new ArrayList<>(), cards, cards.size(), 5, 0);
        return allCardCombinations;
    }

    /***
     * Backtracking algorithm to generate all possible combinations of 5 cards out of 7 cards
     *
     * @param allCardCombinations list of all possible 5 card combinations
     * @param currentCardList temporary currently being generated list
     * @param allCards a list of the 7 cards
     * @param n number of cards in list
     * @param k 5 (need to find 5 cards combination)
     * @param start index/pointer in input
     */
    private void backtrack(ArrayList<ArrayList<Card>> allCardCombinations, ArrayList<Card> currentCardList,
                        List<Card> allCards, int n, int k, int start) {
        if (currentCardList.size() == k)
            allCardCombinations.add(new ArrayList<>(currentCardList));
        else if (currentCardList.size() > k) return;

        for (int i = start; i < n; i++) {
            currentCardList.add(allCards.get(i)); // add a card to currently generated list
            backtrack(allCardCombinations, currentCardList, allCards, n, k, i + 1); // call recursively to generate next card
            currentCardList.remove(currentCardList.size() - 1); // remove last card for next iteration
        }
    }

    /**
     * Method to check if a hand is a flush
     * counts the occurrence of each suit and stores them in a Map
     * @param cards an arraylist of 5 cards
     * @return true IF one suit was 5 times in the list
     */
    private boolean isFlush(ArrayList<Card> cards) {
        Map<Suit, Long> collect = cards.stream()
                .collect(Collectors.groupingBy(Card::suit, Collectors.counting()));
        return collect.containsValue(5L);
    }

    /**
     * Method to check if a hand is a straight
     * @param cards an arraylist of 5 cards
     * @return true IF: Hand has an ACE and the other five cards are in a Straight from 10-K or 2-5
     * ELSE IF the order of each card is always the order of the previous card +1
     */
    private boolean isStraight(ArrayList<Card> cards) {
        int testRank;
        if (getOrderValue(cards, 4) == 14) { // if hand has an ace
            return (getOrderValue(cards, 0) == 2 && getOrderValue(cards, 1) == 3 && getOrderValue(cards, 2) == 4 && getOrderValue(cards, 3) == 5) ||
                    (getOrderValue(cards, 0) == 10 && getOrderValue(cards, 1) == 11 && getOrderValue(cards, 2) == 12 && getOrderValue(cards, 3) == 13);
        } else {
            testRank = getOrderValue(cards, 0) + 1; // if hand does not have an ace
            for (int i = 1; i < 5; i++) {
                if (cards.get(i).rank().getNumericValue() != testRank) return false;  // Straight not found
                testRank++;
            }
            return true;        // Straight found !
        }
    }

    /**
     * Method to check if hand contains a four of a kind
     *
     * @param cards an arraylist of 5 cards
     * @return true IF hand contains FOUR OF A KIND
     */
    private boolean isFourOfAKind(ArrayList<Card> cards) {
        return (getOrderValue(cards, 0) == getOrderValue(cards, 1) && getOrderValue(cards, 1) == getOrderValue(cards, 2) && getOrderValue(cards, 2) == getOrderValue(cards, 3)) ||
                (getOrderValue(cards, 1) == getOrderValue(cards, 2) && getOrderValue(cards, 2) == getOrderValue(cards, 3) && getOrderValue(cards, 3) == getOrderValue(cards, 4));
    }

    /**
     * Method to check for a full house
     *
     * @param cards an arraylist of 5 cards
     * @return true IF hand is a full house
     */
    private boolean isFullHouse(ArrayList<Card> cards) {
        return (getOrderValue(cards, 0) == getOrderValue(cards, 1) && getOrderValue(cards, 1) == getOrderValue(cards, 2) && getOrderValue(cards, 3) == getOrderValue(cards, 4)) ||
                (getOrderValue(cards, 0) == getOrderValue(cards, 1) && getOrderValue(cards, 2) == getOrderValue(cards, 3) && getOrderValue(cards, 3) == getOrderValue(cards, 4));
    }

    /**
     * Method to check for THREE OF A KIND
     *
     * @param cards an arraylist of 5 cards
     * @return true IF hand contains a THREE OF A KIND
     */
    private boolean isThreeOfAKind(ArrayList<Card> cards) {
        boolean a, b, c;
        if (isFourOfAKind(cards) || isFullHouse(cards)) return false;

        a = getOrderValue(cards, 0) == getOrderValue(cards, 1) && getOrderValue(cards, 1) == getOrderValue(cards, 2);
        b = getOrderValue(cards, 1) == getOrderValue(cards, 2) && getOrderValue(cards, 2) == getOrderValue(cards, 3);
        c = getOrderValue(cards, 2) == getOrderValue(cards, 3) && getOrderValue(cards, 3) == getOrderValue(cards, 4);
        return (a || b || c);
    }

    /**
     * Method to check for TWO PAIR
     *
     * @param cards an arraylist of 5 cards
     * @return true IF hand contains two pairs
     */
    private boolean isTwoPairs(ArrayList<Card> cards) {
        boolean a, b, c;

        if (isFourOfAKind(cards) || isFullHouse(cards) || isThreeOfAKind(cards)) return false;
        a = getOrderValue(cards, 0) == getOrderValue(cards, 1) && getOrderValue(cards, 2) == getOrderValue(cards, 3);
        b = getOrderValue(cards, 0) == getOrderValue(cards, 1) && getOrderValue(cards, 3) == getOrderValue(cards, 4);
        c = getOrderValue(cards, 1) == getOrderValue(cards, 2) && getOrderValue(cards, 3) == getOrderValue(cards, 4);
        return (a || b || c);
    }

    /**
     * Method to check for ONE PAIR
     *
     * @param cards an arraylist of 5 cards
     * @return true IF hand contains a pair
     */
    private boolean isOnePair(ArrayList<Card> cards) {
        boolean a, b, c, d;

        if (isFourOfAKind(cards) || isFullHouse(cards) || isThreeOfAKind(cards) || isTwoPairs(cards)) return false;
        a = getOrderValue(cards, 0) == getOrderValue(cards, 1);
        b = getOrderValue(cards, 1) == getOrderValue(cards, 2);
        c = getOrderValue(cards, 2) == getOrderValue(cards, 3);
        d = getOrderValue(cards, 3) == getOrderValue(cards, 4);
        return (a || b || c || d);
    }

    /**
     * Method to sort the cards in ascending order
     *
     * @param cards an arraylist of 5 cards
     */
    private void sortList(ArrayList<Card> cards) {
        Collections.sort(cards);
    }

    /**
     * Get the order value of a card at a specific position in a list of cards.
     *
     * @param cards An arraylist with 5 Cards
     * @param pos the position of the card the order is needed from
     * @return the order of the card (int value from 2 (TWO) - 14(ACE))
     */
    private int getOrderValue(ArrayList<Card> cards, int pos) {
        return cards.get(pos).rank().getNumericValue();
    }

    /**
     * Method to calculate the value of a STRAIGHT FLUSH
     *
     * @param cards an arraylist of 5 cards
     * @return STRAIGHT_FLUSH (8000000) + valueHighCard();
     */
    private int valueStraightFlush(ArrayList<Card> cards) {
        return STRAIGHT_FLUSH + valueHighCard(cards);
    }


    /**
     * Method to calculate the value of a FLUSH
     *
     * @param cards an arraylist of 5 cards
     * @return FLUSH (5000000) + valueHighCard();
     */
    private int valueFlush(ArrayList<Card> cards) {
        return FLUSH + valueHighCard(cards);
    }

    /**
     * Method to calculate the value of a STRAIGHT
     *
     * @param cards an arraylist of 5 cards
     * @return STRAIGHT (4000000) + valueHighCard();
     */
    private int valueStraight(ArrayList<Card> cards) {
        return STRAIGHT + valueHighCard(cards);
    }

    /**
     * Method to calculate the value of a FOUR_OF_A_KIND
     *
     * @param cards an arraylist of 5 cards
     * @return FOUR_OF_A_KIND (7000000) + valueHighCard();
     */
    private int valueFourOfAKind(ArrayList<Card> cards) {
        return FOUR_OF_A_KIND + valueHighCard(cards);
    }

    /**
     * Method to calculate the value of a FULL_HOUSE
     *
     * @param cards an arraylist of 5 cards
     * @return FULL_HOUSE (6000000) + IF xxyyy -> 2* order(x) + 3* order(y) ELSE 3* order(x) + 2* order(y)
     */
    private int valueFullHouse(ArrayList<Card> cards) {
        return FULL_HOUSE + ((getOrderValue(cards, 0) == getOrderValue(cards, 2))
                ? (3 * getOrderValue(cards, 0) + 2 * getOrderValue(cards, 3))
                : (2 * getOrderValue(cards, 0) + 3 * getOrderValue(cards, 3)));
    }


    /**
     * Method to calculate the value of a THREE_OF_A_KIND
     *
     * @param cards an arraylist of 5 cards
     * @return SET (3000000) + IF xxxyz -> 3* order(x) + order(y) + order(z) ELSE 3* order(z) + order(y) + order(x)
     */
    private int valueSet(ArrayList<Card> cards) {
        return SET + ((getOrderValue(cards, 0) == getOrderValue(cards, 2))
                ? (3 * getOrderValue(cards, 0) + getOrderValue(cards, 3) + getOrderValue(cards, 4))
                : (getOrderValue(cards, 0) + getOrderValue(cards, 1) + 3 * getOrderValue(cards, 2)));
    }


    /**
     * Method to calculate value of TWO_PAIRS
     *
     * @param cards an arraylist of 5 cards
     * @return TWO_PAIRS (2000000) + 14^2 * HighPairCard + 14* LowPairCard + UnmatchedCard
     */
    private int valueTwoPairs(ArrayList<Card> cards) {
        int val;
        if (getOrderValue(cards, 0) == getOrderValue(cards, 1) && getOrderValue(cards, 2) == getOrderValue(cards, 3))
            val = (int) (Math.pow(14, 2) * getOrderValue(cards, 2) + 14 * getOrderValue(cards, 0) + getOrderValue(cards, 4));
        else if (getOrderValue(cards, 0) == getOrderValue(cards, 1) && getOrderValue(cards, 3) == getOrderValue(cards, 4))
            val = (int) (Math.pow(14, 2) * getOrderValue(cards, 3) + 14 * getOrderValue(cards, 0) + getOrderValue(cards, 2));
        else
            val = (int) (Math.pow(14, 2) * getOrderValue(cards, 3) + 14 * getOrderValue(cards, 1) + getOrderValue(cards, 0));

        return TWO_PAIRS + val;
    }

    /**
     * Method to calculate value of ONE_PAIR
     * @param cards an arraylist of 5 cards
     * @return ONE_PAIR (1000000) + 14^3 * PairCard + 14^2* HighestCard + 14* MiddleCard + LowestCard
     */
    private int valueOnePair(ArrayList<Card> cards) {
        int val;
        if (getOrderValue(cards, 0) == getOrderValue(cards, 1))
            val = (int) (Math.pow(14, 3) * getOrderValue(cards, 0) + getOrderValue(cards, 2)
                    + 14 * getOrderValue(cards, 3) + Math.pow(14, 2) * getOrderValue(cards, 4));
        else if (getOrderValue(cards, 1) == getOrderValue(cards, 2))
            val = (int) (Math.pow(14, 3) * getOrderValue(cards, 1) + getOrderValue(cards, 0)
                    + 14 * getOrderValue(cards, 3) + Math.pow(14, 2) * getOrderValue(cards, 4));
        else if (getOrderValue(cards, 2) == getOrderValue(cards, 3))
            val = (int) (Math.pow(14, 3) * getOrderValue(cards, 2) + getOrderValue(cards, 0)
                    + 14 * getOrderValue(cards, 1) + Math.pow(14, 2) * getOrderValue(cards, 4));
        else
            val = (int) (Math.pow(14, 3) * getOrderValue(cards, 3) + getOrderValue(cards, 0)
                    + 14 * getOrderValue(cards, 1) + Math.pow(14, 2) * getOrderValue(cards, 2));

        return ONE_PAIR + val;
    }

    /**
     * Method to calculate value of HIGH_CARD
     *
     * @param cards an arraylist of 5 cards
     * @return 14^4* highestCard + 14^3* 2ndHighestCard + 14^2* 3rdHighestCard + 14* 4thHighestCard + LowestCard
     */
    private int valueHighCard(ArrayList<Card> cards) {
        return (int) (getOrderValue(cards, 0)
                        + 14 * getOrderValue(cards, 1)
                        + Math.pow(14, 2) * getOrderValue(cards, 2)
                        + Math.pow(14, 3) * getOrderValue(cards, 3)
                        + Math.pow(14, 4) * getOrderValue(cards, 4));
    }

}
