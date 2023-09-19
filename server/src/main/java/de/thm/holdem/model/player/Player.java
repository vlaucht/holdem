package de.thm.holdem.model.player;

import lombok.Getter;

import java.util.Objects;

/**
 * Class to represent a player.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Getter
public abstract class Player {

    protected final String alias;

    protected int bankroll;

    protected int chips;

    protected int currentBet;

    Player(String alias) {
        this.alias = alias;
        this.bankroll = 10000;
        this.chips = 0;
        this.currentBet = 0;
    }


    /**
     * Method to add chips to the players bankroll.
     *
     * @param amount the amount of chips to add
     */
    public void addToBankroll(int amount) {
        this.bankroll += amount;
    }

    /**
     * Method to remove chips from the players bankroll.
     *
     * @param amount the amount of chips to remove
     */
    public void removeFromBankroll(int amount) {
        this.bankroll -= amount;
    }

    /**
     * Method to leave the game.
     *
     * <p>
     *     Will add the players chips that are not currently bet to the players bankroll and reset the player.
     * </p>
     */
    public void leaveGame() {
        this.bankroll += this.chips;
        this.chips = 0;
        reset();
    }

    /**
     * Method to join the game.
     *
     * <p>
     *     Will remove the initial chips from the players bankroll and add them to the players chips.
     * </p>
     */
    public void joinGame(int buyIn) {
        this.chips = buyIn;
        this.bankroll -= buyIn;
        reset();
    }

    /**
     * Method to reset the player.
     *
     * <p>
     *     Will reset the current bet, the folded state and the hand.
     * </p>
     */
    public void reset() {
        this.currentBet = 0;
    }

    /**
     * Method to add a win to the players chips.
     *
     * @param chips the amount of chips to add
     */
    public void win(int chips) {
        this.chips += chips;
    }

    /**
     * Method to bet chips.
     *
     * <p>
     *     The bet will be removed from the players chips and added to the current bet.
     * </p>
     *
     * @param amount the amount of chips to bet
     */
    public void bet(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Bet amount cannot be negative");
        }
        if (amount > this.chips) {
            throw new IllegalArgumentException("Not enough chips to bet");
        }
        this.chips -= amount;
        this.currentBet += amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return Objects.equals(this.alias, player.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.alias);
    }

    @Override
    public String toString() {
        return this.alias;
    }
}
