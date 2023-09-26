package de.thm.holdem.model.player;

import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.game.poker.PokerPlayerAction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PokerPlayer extends Player {

    /** The score of the players hand */
    private int handScore;

    /** The cards the player has on his hand */
    @Setter
    protected List<Card> hand;

    /** The last action performed by the player */
    protected PokerPlayerAction lastAction;

    /** Indicates if the player has folded */
    @Accessors(fluent = true)
    private boolean isFolded;

    @Getter
    private final List<PokerPlayerAction> allowedActions;

    @Setter
    private boolean mustShowCards;

    @Setter
    private BigInteger potShare;


    public PokerPlayer(String id, String alias, String avatar, BigInteger bankroll) {
        super(id, alias, avatar, bankroll);
        this.handScore = 0;
        this.hand = new ArrayList<>(2);
        this.isFolded = false;
        this.allowedActions = new ArrayList<>();
        this.potShare = BigInteger.ZERO;
    }

    public boolean canDoAction(PokerPlayerAction allowedAction) {
        return this.allowedActions.contains(allowedAction);
    }

    public boolean hasHand() {
        return hand.size() == 2;
    }

    /**
     * Method to deal a card to the players hand.
     *
     * @param card the card to be dealt
     */
    public void dealCard(Card card) throws GameActionException {
        if (this.hand.size() >= 2) {
            throw new GameActionException("Player already has two cards.");
        }
        this.hand.add(card);
    }

    public void payBigBlind(BigInteger bigBlind) {
        lastAction = PokerPlayerAction.BIG_BLIND;
        chips = chips.subtract(bigBlind);
        currentBet = currentBet.add(bigBlind);
    }

    public void paySmallBlind(BigInteger smallBlind) {
        lastAction = PokerPlayerAction.SMALL_BLIND;
        chips = chips.subtract(smallBlind);
        currentBet = currentBet.add(smallBlind);
    }

    public void call(BigInteger bet) {
        lastAction = PokerPlayerAction.CALL;
        chips = chips.subtract(bet);
        currentBet = currentBet.add(bet);
    }

    public void clearAllowedActions() {
        this.allowedActions.clear();
    }

    public void addAllowedAction(PokerPlayerAction action) {
        this.allowedActions.add(action);
    }

    public void check() {
        lastAction = PokerPlayerAction.CHECK;
    }

    public void setLastAction(PokerPlayerAction action) {
        this.lastAction = action;
    }

    /** Indicates if the player is not participating anymore but still in the game (e.g. no cash left) */
    public boolean isSpectator() {
        return hand.size() == 0 && chips.equals(BigInteger.ZERO);
    }


    public boolean isAllIn() {
        return hand.size() > 0 && chips.equals(BigInteger.ZERO);
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
        this.handScore = 0;
        this.hand.clear();
        this.allowedActions.clear();
        this.mustShowCards = false;
        this.potShare = BigInteger.ZERO;
    }


    public void setHandScore(int score) {
        this.handScore = score;
    }

}
