package de.thm.holdem.model.game.poker;

import de.thm.holdem.model.player.PokerPlayer;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a pot in a poker game.
 */
public class Pot {


    /**
     * Players that have contributed to this pot and the contribution.
     */
    protected final Map<PokerPlayer, BigInteger> contributions;

    public Pot() {
        contributions = new HashMap<>();
    }


    public BigInteger getPotSize() {
        BigInteger totalPotSize = BigInteger.ZERO;
        for (BigInteger contribution : contributions.values()) {
            totalPotSize = totalPotSize.add(contribution);
        }
        return totalPotSize;
    }

    public PokerPlayer getAllInPlayerWithSmallestStack() {
        // Filter out the allIn contributors
        Map<PokerPlayer, BigInteger> allInContributors = contributions.entrySet().stream()
                .filter(entry -> entry.getKey().isAllIn())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (allInContributors.isEmpty()) {
            // No allIn contributors
            return null;
        }

        // Find the highest contribution amount
        BigInteger highestContribution = allInContributors.values().stream()
                .max(BigInteger::compareTo)
                .orElse(BigInteger.ZERO);

        // Find the allIn contributor with the least contribution
        Optional<PokerPlayer> allInContributorWithLeastContribution = allInContributors.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(highestContribution) < 0)
                .map(Map.Entry::getKey)
                .min(Comparator.comparing(contributions::get));

        return allInContributorWithLeastContribution.orElse(null);
    }



    /**
     * Returns the contributing players.
     *
     * @return The contributing players.
     */
    public Set<PokerPlayer> getContributors() {
        return contributions.keySet();
    }

    /**
     * Adds a player to the contributors.
     *
     * @param player The player to add.
     */
    public void addContributor(PokerPlayer player, BigInteger contribution) {
        contributions.put(player, contribution);
    }


    public Set<PokerPlayer> getWinners() {

        Set<PokerPlayer> contributors = getContributors();

        // Find the highest handScore among contributors
        int highestHandScore = contributors.stream()
                .filter(player -> !player.isFolded())
                .mapToInt(PokerPlayer::getHandScore)
                .max()
                .orElse(0);

        // return players with the highest handScore and who have not folded
        return contributors.stream()
                .filter(player -> !player.isFolded() && player.getHandScore() == highestHandScore)
                .collect(Collectors.toSet());
    }



    public Pot split(PokerPlayer allInPlayerWithSmallestStack) {
        Pot sidePot = new Pot();
        BigInteger allInContribution = getPlayerContribution(allInPlayerWithSmallestStack);
        for (PokerPlayer player : contributions.keySet()) {
            if (player.equals(allInPlayerWithSmallestStack)) continue;
            BigInteger contribution = getPlayerContribution(player);
            BigInteger remainder = contribution.subtract(allInContribution);
            // if the player has fewer chips in the pot than the allIn player (can happen if the player has folded),
            // his contributions stays in this pot, and he is not added to the side pot
            if (remainder.compareTo(BigInteger.ZERO) <= 0) continue;

            contributions.merge(player, remainder, BigInteger::subtract);

            sidePot.addContributor(player, remainder);


        }
        return sidePot;
    }


    public BigInteger getPlayerContribution(PokerPlayer player) {
        return contributions.getOrDefault(player, BigInteger.ZERO);
    }


    public void contribute(PokerPlayer player, BigInteger amount) {
        BigInteger playerContribution = getPlayerContribution(player);
        contributions.put(player, playerContribution.add(amount));
    }

    /**
     * Clears the pot.
     */
    public void clear() {
        contributions.clear();
    }


}
