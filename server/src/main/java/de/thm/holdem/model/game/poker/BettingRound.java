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


    NONE(0), PRE_FLOP(1), FLOP(2), TURN(3), RIVER(4), END(5);

    private final int order;
    BettingRound(int order) {
        this.order = order;
    }

    public boolean isBefore(BettingRound targetRound) {
        return this.order < targetRound.order;
    }

    public boolean isAfter(BettingRound targetRound) {
        return this.order > targetRound.order;
    }

}
