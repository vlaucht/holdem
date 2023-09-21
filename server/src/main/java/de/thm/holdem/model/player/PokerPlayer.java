package de.thm.holdem.model.player;

import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.game.poker.PokerPlayerAction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PokerPlayer extends Player {

    /** boolean to indicate if the player is folded */
    @Accessors(fluent = true)
    protected boolean isFolded;

    /** boolean to indicate if the player is all in */
    @Accessors(fluent = true)
    protected boolean isAllIn;

    /** The score of the players hand */
    private int handScore;

    /** The cards the player has on his hand */
    @Setter
    protected List<Card> hand;

    /** The last action performed by the player */
    @Setter
    protected PokerPlayerAction lastAction;

    public PokerPlayer(String alias) {
        super(alias);
        this.isFolded = false;
        this.handScore = 0;
        this.hand = new ArrayList<>(2);
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



    /**
     * Method to fold the players hand.
     */
    public void fold() {
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
        this.isAllIn = false;
        this.lastAction = null;
        this.handScore = 0;
        this.hand.clear();
    }


    public void setHandScore(int score) {
        System.out.println(this);
        this.handScore = score;
    }

}
