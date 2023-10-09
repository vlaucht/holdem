package de.thm.holdem.model.player;

import de.thm.holdem.exception.GameActionException;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Class to represent a player.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Getter
public abstract class Player {

    /** The id of the player. */
    protected final String id;

    /** The nickname of the player. */
    protected final String alias;

    /** The avatar of the player. */
    protected final String avatar;

    /** The bankroll of the player. */
    protected BigInteger bankroll;

    /** The amount of chips the player can play with in the current game. */
    protected BigInteger chips;

    /** The amount of chips the player has bet in the current round. */
    protected BigInteger currentBet;

    /**
     * Constructor to create a player.
     *
     * @param id the id of the player.
     * @param alias the nickname of the player.
     * @param avatar the avatar of the player.
     * @param bankroll the bankroll of the player.
     */
    Player(String id, String alias, String avatar, BigInteger bankroll) {
        this.id = id;
        this.alias = alias;
        this.avatar = avatar;
        this.bankroll = bankroll;
        this.chips = BigInteger.ZERO;
        this.currentBet = BigInteger.ZERO;
    }

    public void setCurrentBet(BigInteger amount) {
        this.currentBet = amount;
    }


    /**
     * Method to add chips to the players bankroll.
     *
     * @param amount the amount of chips to add
     */
    void addToBankroll(BigInteger amount) {
        bankroll = bankroll.add(amount);
    }

    /**
     * Method to remove chips from the players bankroll.
     *
     * @param amount the amount of chips to remove
     */
    public void removeFromBankroll(BigInteger amount) {
        bankroll = bankroll.subtract(amount);
    }

    /**
     * Method to leave the game.
     *
     * <p>
     *     Will add the players chips that are not currently bet to the players bankroll and reset the player.
     * </p>
     *
     * @return the remaining bankroll after the leave.
     */
    public BigInteger leaveGame() {
        addToBankroll(chips);
        chips = BigInteger.ZERO;
        reset();
        return bankroll;
    }

    /**
     * Method to join the game.
     *
     * <p>
     *     Will remove the initial chips from the players bankroll and add them to the players chips.
     * </p>
     *
     * @param buyIn the amount of chips to buy in.
     * @return the remaining bankroll after the buy in.
     */
    public BigInteger joinGame(BigInteger buyIn) {
        chips = buyIn;
        removeFromBankroll(buyIn);
        reset();
        return bankroll;
    }

    /**
     * Method to reset the player.
     *
     * <p>
     *     Will reset the current bet, the folded state and the hand.
     * </p>
     */
    public void reset() {
        this.currentBet = BigInteger.ZERO;
    }

    /**
     * Method to add a win to the players chips.
     *
     * @param amount the amount of chips to add
     */
    public void win(BigInteger amount) {
        chips = chips.add(amount);
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
    public void bet(BigInteger amount) throws GameActionException {
        if (amount.compareTo(BigInteger.ZERO) <= 0) {
            throw new GameActionException("Bet amount has to be greater than 0.");
        }
        if (amount.compareTo(chips) > 0) {
            throw new GameActionException("Not enough chips to bet.");
        }
        chips = chips.subtract(amount);
        currentBet = currentBet.add(amount);
    }

    /**
     * Method to compare two players.
     *
     * @param obj the player to compare to.
     * @return true if the unique id of the players are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return Objects.equals(this.id, player.id);
    }

    /**
     * Method to get the hash code of the player.
     *
     * @return the hash code of the player.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
