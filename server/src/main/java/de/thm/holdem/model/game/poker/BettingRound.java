package de.thm.holdem.model.game.poker;

/**
 * Enum which returns all possible states the game can be in
 *
 * <p>
 *      PRE_FLOP: Each player has 2 Cards on his hands and the players can bet
 *      FLOP: First 3 cards on the table will be flipped
 *      TURN: 4th card on the table will be flipped
 *      RIVER: 5th card on the table will be flipped
 *      END: Hands of the players will be evaluated and round ends
 * </p>
 */
public enum BettingRound {


    PRE_FLOP(0), FLOP(1), TURN(2), RIVER(3), END(4);

    private final int order;
    BettingRound(int order) {
        this.order = order;
    }

    public boolean isBefore(BettingRound targetRound) {
        return this.order < targetRound.order;
    }

}
