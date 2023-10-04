package de.thm.holdem.model.game.poker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BettingRoundTest {

    @Test
    void Should_ReturnTrue_When_Before() {
        assertTrue(BettingRound.PRE_FLOP.isBefore(BettingRound.FLOP));
        assertTrue(BettingRound.PRE_FLOP.isBefore(BettingRound.TURN));
        assertTrue(BettingRound.PRE_FLOP.isBefore(BettingRound.RIVER));
        assertTrue(BettingRound.PRE_FLOP.isBefore(BettingRound.END));
        assertTrue(BettingRound.FLOP.isBefore(BettingRound.TURN));
        assertTrue(BettingRound.FLOP.isBefore(BettingRound.RIVER));
        assertTrue(BettingRound.FLOP.isBefore(BettingRound.END));
        assertTrue(BettingRound.TURN.isBefore(BettingRound.RIVER));
        assertTrue(BettingRound.TURN.isBefore(BettingRound.END));
        assertTrue(BettingRound.RIVER.isBefore(BettingRound.END));
    }

    @Test
    void Should_ReturnFalse_When_NotBefore() {
        assertFalse(BettingRound.PRE_FLOP.isBefore(BettingRound.PRE_FLOP));
        assertFalse(BettingRound.FLOP.isBefore(BettingRound.PRE_FLOP));
        assertFalse(BettingRound.TURN.isBefore(BettingRound.PRE_FLOP));
        assertFalse(BettingRound.RIVER.isBefore(BettingRound.PRE_FLOP));
        assertFalse(BettingRound.END.isBefore(BettingRound.PRE_FLOP));
        assertFalse(BettingRound.FLOP.isBefore(BettingRound.FLOP));
        assertFalse(BettingRound.TURN.isBefore(BettingRound.FLOP));
        assertFalse(BettingRound.RIVER.isBefore(BettingRound.FLOP));
        assertFalse(BettingRound.END.isBefore(BettingRound.FLOP));
        assertFalse(BettingRound.TURN.isBefore(BettingRound.TURN));
        assertFalse(BettingRound.RIVER.isBefore(BettingRound.TURN));
        assertFalse(BettingRound.END.isBefore(BettingRound.TURN));
        assertFalse(BettingRound.RIVER.isBefore(BettingRound.RIVER));
        assertFalse(BettingRound.END.isBefore(BettingRound.RIVER));
        assertFalse(BettingRound.END.isBefore(BettingRound.END));
    }

    @Test
    void Should_ReturnTrue_When_IsAfter() {
        assertTrue(BettingRound.FLOP.isAfter(BettingRound.PRE_FLOP));
        assertTrue(BettingRound.TURN.isAfter(BettingRound.PRE_FLOP));
        assertTrue(BettingRound.RIVER.isAfter(BettingRound.PRE_FLOP));
        assertTrue(BettingRound.END.isAfter(BettingRound.PRE_FLOP));
        assertTrue(BettingRound.TURN.isAfter(BettingRound.FLOP));
        assertTrue(BettingRound.RIVER.isAfter(BettingRound.FLOP));
        assertTrue(BettingRound.END.isAfter(BettingRound.FLOP));
        assertTrue(BettingRound.RIVER.isAfter(BettingRound.TURN));
        assertTrue(BettingRound.END.isAfter(BettingRound.TURN));
        assertTrue(BettingRound.END.isAfter(BettingRound.RIVER));
    }

    @Test
    void Should_ReturnFalse_When_NotAfter() {
        assertFalse(BettingRound.PRE_FLOP.isAfter(BettingRound.PRE_FLOP));
        assertFalse(BettingRound.PRE_FLOP.isAfter(BettingRound.FLOP));
        assertFalse(BettingRound.PRE_FLOP.isAfter(BettingRound.TURN));
        assertFalse(BettingRound.PRE_FLOP.isAfter(BettingRound.RIVER));
        assertFalse(BettingRound.PRE_FLOP.isAfter(BettingRound.END));
        assertFalse(BettingRound.FLOP.isAfter(BettingRound.FLOP));
        assertFalse(BettingRound.FLOP.isAfter(BettingRound.TURN));
        assertFalse(BettingRound.FLOP.isAfter(BettingRound.RIVER));
        assertFalse(BettingRound.FLOP.isAfter(BettingRound.END));
        assertFalse(BettingRound.TURN.isAfter(BettingRound.TURN));
        assertFalse(BettingRound.TURN.isAfter(BettingRound.RIVER));
        assertFalse(BettingRound.TURN.isAfter(BettingRound.END));
        assertFalse(BettingRound.RIVER.isAfter(BettingRound.RIVER));
        assertFalse(BettingRound.RIVER.isAfter(BettingRound.END));
        assertFalse(BettingRound.END.isAfter(BettingRound.END));
    }

}