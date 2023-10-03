package de.thm.holdem.model.player;

import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.game.poker.PokerHand;
import de.thm.holdem.model.game.poker.PokerPlayerAction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a poker player.
 *
 * @see Player
 * @author Valentin Laucht
 * @version 1.0
 */
@Getter
public class PokerPlayer extends Player {

    /** The cards the player has on his hand */
    @Setter
    protected List<Card> holeCards;

    /** The last action performed by the player */
    protected PokerPlayerAction lastAction;

    /** Indicates if the player has folded */
    @Accessors(fluent = true)
    private boolean isFolded;

    /** A list of actions the player is allowed to perform */
    @Getter
    private final List<PokerPlayerAction> allowedActions;

    /** Indicates if the player must show his cards when the round ends. */
    @Setter
    @Accessors(fluent = true)
    private boolean mustShowCards;

    /** The share of the pot the player will get */
    @Setter
    private BigInteger potShare;

    /** The current {@link PokerHand} of the player */
    private final PokerHand hand;

    /**
     * Constructor to create a new poker player.
     *
     * @param id the id of the player.
     * @param alias the alias of the player.
     * @param avatar the avatar of the player.
     * @param bankroll the bankroll of the player.
     */
    public PokerPlayer(String id, String alias, String avatar, BigInteger bankroll) {
        super(id, alias, avatar, bankroll);
        this.holeCards = new ArrayList<>(2);
        this.isFolded = false;
        this.allowedActions = new ArrayList<>();
        this.potShare = BigInteger.ZERO;
        this.hand = new PokerHand();
    }

    /**
     * Method to check if the player is allowed to perform the given action.
     *
     * @param allowedAction the action to be checked.
     * @return true if the player is allowed to perform the action, false otherwise.
     */
    public boolean canDoAction(PokerPlayerAction allowedAction) {
        return this.allowedActions.contains(allowedAction);
    }

    /**
     * Method to check if the player has two hole cards.
     *
     * @return true if the player has two hole cards, false otherwise.
     */
    public boolean hasHoleCards() {
        return holeCards.size() == 2;
    }

    /**
     * Method to deal a card to the players hand.
     *
     * @param card the card to be dealt
     */
    public void dealCard(Card card) throws GameActionException {
        if (this.holeCards.size() >= 2) {
            throw new GameActionException("Player already has two cards.");
        }
        this.holeCards.add(card);
    }

    /**
     * Method to get the players hand score.
     *
     * <p>
     *     If the player has folded or is a spectator, the score will be 0.
     * </p>
     *
     * @return the players hand score.
     */
    public int getHandScore() {
        if (hand.getHandResult() == null || isFolded || isSpectator()) {
            return 0;
        }
        return hand.getHandResult().getHandValue();
    }

    /**
     * Method to pay the big blind.
     *
     * <p>
     *     The players last action will be set to big blind and the players chips will be reduced by the big blind.
     *     The current bet will be increased by the big blind.
     * </p>
     *
     * @param bigBlind the amount of the big blind.
     */
    public void payBigBlind(BigInteger bigBlind) {
        lastAction = PokerPlayerAction.BIG_BLIND;
        chips = chips.subtract(bigBlind);
        currentBet = currentBet.add(bigBlind);
    }

    /**
     * Method to pay the small blind.
     *
     * <p>
     *     The players last action will be set to small blind and the players chips will be reduced by the small blind.
     *     The current bet will be increased by the small blind.
     * </p>
     *
     * @param smallBlind the amount of the small blind.
     */
    public void paySmallBlind(BigInteger smallBlind) {
        lastAction = PokerPlayerAction.SMALL_BLIND;
        chips = chips.subtract(smallBlind);
        currentBet = currentBet.add(smallBlind);
    }

    /**
     * Method to call a bet.
     *
     * <p>
     *     The players last action will be set to call and the players chips will be reduced by the bet.
     *     The current bet will be increased by the bet.
     * </p>
     *
     * @param bet the amount needed to call the bet.
     */
    public void call(BigInteger bet) {
        lastAction = PokerPlayerAction.CALL;
        chips = chips.subtract(bet);
        currentBet = currentBet.add(bet);
    }

    /**
     *  Method to clear all allowed actions.
     */
    public void clearAllowedActions() {
        this.allowedActions.clear();
    }

    /**
     * Method to add an action the player is allowed to perform.
     *
     * @param action the action to be added.
     */
    public void addAllowedAction(PokerPlayerAction action) {
        this.allowedActions.add(action);
    }

    /**
     * Method to check the players hand.
     */
    public void check() {
        lastAction = PokerPlayerAction.CHECK;
    }

    /**
     * Method to set the last action performed by the player.
     *
     * @param action the last action performed by the player
     */
    public void setLastAction(PokerPlayerAction action) {
        this.lastAction = action;
    }

    /** Indicates if the player is not participating anymore but still in the game (e.g. no cash left) */
    public boolean isSpectator() {
        return holeCards.size() == 0 && chips.equals(BigInteger.ZERO);
    }


    /**
     * Indicates if the player is all in.
     *
     * <p>
     *     A player is all in if he has no chips left but still has cards on his hand.
     * </p>
     *
     * @return true if the player is all in, false otherwise
     */
    public boolean isAllIn() {
        return holeCards.size() > 0 && chips.equals(BigInteger.ZERO);
    }

    /**
     * Method to fold the players hand.
     */
    public void fold() {
        this.lastAction = PokerPlayerAction.FOLD;
        this.isFolded = true;
    }


    /**
     * Method to reset the player.
     *
     * <p>
     *     Will reset the current bet, the folded state and the hand.
     * </p>
     */
    public void reset() {
        super.reset();
        this.isFolded = false;
        this.lastAction = null;
        this.holeCards.clear();
        this.allowedActions.clear();
        this.mustShowCards = false;
        this.potShare = BigInteger.ZERO;
        this.hand.reset();
    }

}
