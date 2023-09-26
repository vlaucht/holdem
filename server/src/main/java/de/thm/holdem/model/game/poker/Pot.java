package de.thm.holdem.model.game.poker;

import de.thm.holdem.model.player.Player;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a pot in a poker game.
 */
public class Pot {

        /** Current bet of each player in the pot. */
        private BigInteger bet;

        /** Players that have contributed to this pot. */
        public final Set<Player> contributors;

        public Pot(BigInteger bet) {
            this.bet = bet;
            contributors = new HashSet<>();
        }

        /**
         * Returns the current bet of each player in the pot.
         *
         * @return The current bet.
         */
        public BigInteger getBet() {
            return bet;
        }

        /**
         * Returns the contributing players.
         *
         * @return The contributing players.
         */
        public Set<Player> getContributors() {
            return contributors;
        }

        /**
         * Adds a contributing player to the pot.
         *
         * @param player The player.
         */
        public void addContributor(Player player) {
            contributors.add(player);
        }

        /**
         * Checks whether a specific player has contributed to this pot.
         *
         * @param player The player to check for
         * @return True if the player has contributed, false otherwise.
         */
        public boolean hasContributed(Player player) {
            return contributors.contains(player);
        }

        /**
         * Returns the total value of this pot.
         *
         * <p>
         *     This is the bet multiplied by the number of contributors.
         * </p>
         *
         * @return The total value.
         */
        public BigInteger getValue() {
            return bet.multiply(BigInteger.valueOf(contributors.size()));
        }

        /**
         * In case of a partial call, bet or raise, splits this pot into two pots,
         * with this pot keeping the lower bet and the other pot the remainder.
         *
         * @param player The player with the partial call, bet or raise.
         * @param partialBet The amount of the partial bet.
         * @return The other pot, with the remainder.
         */
        public Pot split(Player player, BigInteger partialBet) {
            Pot pot = new Pot(bet.subtract(partialBet));
            for (Player contributor : contributors) {
                pot.addContributor(contributor);
            }
            bet = partialBet;
            contributors.add(player);
            return pot;
        }

        /**
         * Clears this pot.
         */
        public void clear() {
            bet = BigInteger.ZERO;
            contributors.clear();
        }


}
