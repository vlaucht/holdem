package de.thm.holdem.model.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlindHelperTest {

    @Test
    public void Should_CalculateBlindLevels() {
        int numOfPlayers = 5;
        int buyIn = 1000;
        int totalTournamentTime = 180;
        int timeToRaiseBlinds = 20;

        List<Integer> blindLevels = BlindHelper.calculateBlindLevels(
                numOfPlayers, buyIn, totalTournamentTime, timeToRaiseBlinds
        );

        assertNotNull(blindLevels);
        assertEquals(9, blindLevels.size());
        assertEquals(25, blindLevels.get(0));
        assertEquals(30, blindLevels.get(1));
        assertEquals(50, blindLevels.get(2));
        assertEquals(65, blindLevels.get(3));
        assertEquals(100, blindLevels.get(4));
        assertEquals(150, blindLevels.get(5));
        assertEquals(225, blindLevels.get(6));
        assertEquals(335, blindLevels.get(7));
        assertEquals(500, blindLevels.get(8));
    }
}