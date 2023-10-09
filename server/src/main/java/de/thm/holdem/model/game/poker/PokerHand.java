package de.thm.holdem.model.game.poker;

import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Rank;
import de.thm.holdem.model.card.Suit;

import java.util.*;

/**
 * Class to represent a poker hand of up to 5 cards.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public class PokerHand {

    /** A list of up to 5 cards that make up the hand. */
    private List<Card> cards;

    /** The result of the hand evaluation. */
    private PokerHandResult handResult;

    /**
     * Constructor to create a new PokerHand.
     */
    public PokerHand() {
        reset();
    }

    /**
     * Method to reset the hand.
     */
    public void reset() {
        this.cards = new ArrayList<>();
        handResult = null;
    }

    /**
     * Method to add one card to the hand.
     *
     * <p>
     *     If the total amount of cards in the hand is more than 5 cards, the best 5-card-combination is chosen.
     *     The {@link #evaluateHand(List)} is used method to evaluate the hand.
     *     The result of the hand evaluation is stored in the {@link #handResult} field.
     *     The {@link #cards} field is updated with the cards that make up the best 5-card-combination.
     * </p>
     * @param card the card to add to the hand.
     */
    public void addCard(Card card) {
        addCards(Collections.singletonList(card));
    }

    /**
     * Method to add a list of cards to the hand.
     *
     * <p>
     *     If the total amount of cards in the hand is more than 5 cards, the best 5-card-combination is chosen.
     *     The {@link #evaluateHand(List)} is used method to evaluate the hand.
     *     The result of the hand evaluation is stored in the {@link #handResult} field.
     *     The {@link #cards} field is updated with the cards that make up the best 5-card-combination.
     * </p>
     * @param newCards the cards to add to the hand.
     */
    public void addCards(List<Card> newCards) {
        this.cards.addAll(newCards);

        if (this.cards.size() <= 5) {
            this.handResult = evaluateHand(this.cards);
        } else {
            // ArrayList which will store all 21 5-Card-Combinations out of 7 cards
            ArrayList<ArrayList<Card>> allCardCombinations = find5CardsCombinations(new ArrayList<>(this.cards));

            PokerHandResult bestHandResult = null;

            for (List<Card> combination : allCardCombinations) {
                PokerHandResult currentHandResult = evaluateHand(combination);

                if (bestHandResult == null || currentHandResult.compareTo(bestHandResult) > 0) {
                    bestHandResult = currentHandResult;
                    this.cards = new ArrayList<>(combination); // Update cards with the best combination
                }
            }

            handResult = bestHandResult;
        }
    }

    /***
     * Method generates all possible combinations of 5 cards out of 7 cards
     *
     * @param cards an arraylist of 7 cards
     * @return all the possible 5 card combinations
     */
    ArrayList<ArrayList<Card>> find5CardsCombinations(ArrayList<Card> cards) {
        ArrayList<ArrayList<Card>> allCardCombinations = new ArrayList<>();
        backtrack(allCardCombinations, new ArrayList<>(), cards, cards.size(), 5, 0);
        return allCardCombinations;
    }

    /***
     * Backtracking algorithm to generate all possible combinations of 5 cards out of 7 cards
     *
     * @param allCardCombinations list of all possible 5 card combinations
     * @param currentCardList temporary currently being generated list
     * @param allCards a list of up to 7 cards
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
     * Method to get the cards of the hand.
     *
     * @return a list of the cards of the hand.
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Method to get the result of the hand evaluation.
     *
     * @return the {@link PokerHandResult} of the hand evaluation.
     */
    public PokerHandResult getHandResult() {
        return handResult;
    }

    /**
     * Method to evaluate the hand.
     */
    public PokerHandResult evaluateHand(List<Card> cardsToEvaluate) {
        sortHandDescending(cardsToEvaluate);

        // list of ranks of the hand
        List<Integer> ranks = new ArrayList<>();

        // map that stores the number of cards for each rank
        Map<Integer, Integer> rankCounts = new HashMap<>();

        // count the number of cards for each rank
        for (Card card : cardsToEvaluate) {
            int rank = card.rank().getValue();
            ranks.add(rank);
            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
        }

        // list that stores only the cards that are part of the hand type
        List<Card> handCards = new ArrayList<>();

        // default hand type is high card
        PokerHandType handType = PokerHandType.HIGH_CARD;

        // check if hand contains FOUR_OF_A_KIND
        if (doesRankCountContain(rankCounts, 4)) {
            handType = PokerHandType.FOUR_OF_A_KIND;
            handCards = findXOfAKindCards(ranks, 4, cardsToEvaluate);
        // check if hand contains FULL_HOUSE
        } else if (doesRankCountContain(rankCounts, 3) && doesRankCountContain(rankCounts, 2)) {
            handType = PokerHandType.FULL_HOUSE;
            handCards.addAll(cardsToEvaluate);
        // check if hand contains STRAIGHT_FLUSH
        } else if (isStraight(ranks) && isFlush(cardsToEvaluate)) {
        handType = PokerHandType.STRAIGHT_FLUSH;
        handCards.addAll(cardsToEvaluate);
            // If the highest card is an Ace, it's a Royal Flush
            if (cardsToEvaluate.get(0).rank() == Rank.ACE) {
                handType = PokerHandType.ROYAL_FLUSH;
            }
        // check if hand contains FLUSH
        } else if (isFlush(cardsToEvaluate)) {
            handType = PokerHandType.FLUSH;
            handCards.addAll(cardsToEvaluate);
        // check if hand contains STRAIGHT
        } else if (isStraight(ranks)) {
            handType = PokerHandType.STRAIGHT;
            handCards.addAll(cardsToEvaluate);
        // check if hand contains THREE_OF_A_KIND
        } else if (doesRankCountContain(rankCounts, 3)) {
            handType = PokerHandType.THREE_OF_A_KIND;
            handCards = findXOfAKindCards(ranks, 3, cardsToEvaluate);
        // check if hand contains ONE_PAIR
        } else if (doesRankCountContain(rankCounts, 2)) {
            int pairRank = findPairRank(rankCounts);
            int remainingPairRank = findRemainingPairRank(rankCounts, pairRank);
            // check if hand contains TWO_PAIRS
            if (remainingPairRank > 0) {
                handType = PokerHandType.TWO_PAIRS;
                handCards = findPairsCards(pairRank, remainingPairRank, cardsToEvaluate);
            // hand contains only ONE_PAIR
            } else {
                handType = PokerHandType.ONE_PAIR;
                handCards = findPairsCards(pairRank, 0, cardsToEvaluate);
            }
        } else {
            // hand contains only HIGH_CARD
            handCards.add(cardsToEvaluate.get(0));
        }
        return new PokerHandResult(handType, new ArrayList<>(cardsToEvaluate), new ArrayList<>(handCards));
    }

    /**
     * Method to find the cards that make up one or two pairs.
     *
     * @param pairRank1 the rank of the first pair.
     * @param pairRank2 the rank of the second pair, provide 0 if only one pair exists.
     * @param cardsToEvaluate a list of cards to evaluate.
     * @return a list of the cards that make up one or two pairs.
     */
    private List<Card> findPairsCards(int pairRank1, int pairRank2, List<Card> cardsToEvaluate) {
        List<Card> pairsCards = new ArrayList<>();
        for (Card card : cardsToEvaluate) {
            int rank = card.rank().getValue();
            if (rank == pairRank1 || rank == pairRank2) {
                pairsCards.add(card);
            }
        }
        return pairsCards;
    }


    /**
     * Method to find the cards that form a specified count of a Kind.
     *
     * @param ranks      a list of ranks of the hand.
     * @param targetCount the count to search for (e.g., 3 for Three of a Kind, 4 for Four of a Kind).
     * @param cardsToEvaluate a list of cards to evaluate.
     * @return a list of the cards that form the specified count of a Kind.
     */
    private List<Card> findXOfAKindCards(List<Integer> ranks, int targetCount, List<Card> cardsToEvaluate) {
        List<Card> xOfAKindCards = new ArrayList<>();
        for (Card card : cardsToEvaluate) {
            int rank = card.rank().getValue();
            if (Collections.frequency(ranks, rank) == targetCount) {
                xOfAKindCards.add(card);
            }
        }
        return xOfAKindCards;
    }


    /**
     * Method to check whether the rankCount map contains a certain amount of cards of the same rank.
     *
     * @param rankCounts a map that stores the number of cards for each rank.
     * @param count the amount of cards of the same rank that is looked for.
     * @return true if the rankCount map contains the required amount of cards of the same rank, false otherwise.
     */
    private boolean doesRankCountContain(Map<Integer, Integer> rankCounts, int count) {
        return rankCounts.containsValue(count);
    }


    /**
     * Method to find the rank of a second pair in a hand.
     *
     * @param rankCounts a map that stores the number of cards for each rank.
     * @param pairRank the rank of the first pair.
     * @return the rank of the second pair, 0 if no second pair is found.
     */
    private int findRemainingPairRank(Map<Integer, Integer> rankCounts, int pairRank) {
        for (Map.Entry<Integer, Integer> entry : rankCounts.entrySet()) {
            if (entry.getValue() >= 2 && entry.getKey() != pairRank) {
                return entry.getKey();
            }
        }
        return 0;
    }


    /**
     * Method to find the rank of the cards that make up a pair.
     *
     * @param rankCounts a map that stores the number of cards for each rank.
     * @return the rank of the cards that make up a pair, 0 if no pair is found.
     */
    private int findPairRank(Map<Integer, Integer> rankCounts) {
        for (Map.Entry<Integer, Integer> entry : rankCounts.entrySet()) {
            if (entry.getValue() >= 2) {
                return entry.getKey();
            }
        }
        return 0; // Return 0 if no pair is found
    }


    /**
     * Sort the hand by rank value in descending order.
     *
     * @param cardsToSort a list of cards to sort.
     */
    public void sortHandDescending(List<Card> cardsToSort) {
        cardsToSort.sort(Collections.reverseOrder());
    }

    /**
     * Method to check whether the hand is a flush.
     *
     * @param cards a list of cards of the hand.
     * @return true if the hand is a flush, false otherwise.
     */
    private boolean isFlush(List<Card> cards) {
        Suit firstSuit = cards.get(0).suit();
        for (Card card : cards) {
            if (card.suit() != firstSuit) {
                return false;
            }
        }
        return true;
    }


    /**
     * Method to check whether the hand is a straight.
     *
     * @param ranks a list of ranks of the hand.
     * @return true if the hand is a straight, false otherwise.
     */
    private boolean isStraight(List<Integer> ranks) {
        for (int i = 0; i < ranks.size() - 1; i++) {
            if (ranks.get(i) != ranks.get(i + 1) + 1) {
                return false;
            }
        }
        return true;
    }

}
