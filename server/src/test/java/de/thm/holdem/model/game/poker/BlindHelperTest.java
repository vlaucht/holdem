package de.thm.holdem.model.game.poker;

import de.thm.holdem.model.game.poker.BlindHelper;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlindHelperTest {

    @Test
    void Should_CalculateBlindLevels() {
        int numOfPlayers = 5;
        BigInteger buyIn = BigInteger.valueOf(1000);
        int totalTournamentTime = 180;
        int timeToRaiseBlinds = 20;

        List<BigInteger> blindLevels = BlindHelper.calculateBlindLevels(
                numOfPlayers, buyIn, totalTournamentTime, timeToRaiseBlinds
        );

        assertNotNull(blindLevels);
        assertEquals(9, blindLevels.size());
        assertEquals(BigInteger.valueOf(25), blindLevels.get(0));
        assertEquals(BigInteger.valueOf(30), blindLevels.get(1));
        assertEquals(BigInteger.valueOf(50), blindLevels.get(2));
        assertEquals(BigInteger.valueOf(65), blindLevels.get(3));
        assertEquals(BigInteger.valueOf(100), blindLevels.get(4));
        assertEquals(BigInteger.valueOf(150), blindLevels.get(5));
        assertEquals(BigInteger.valueOf(225), blindLevels.get(6));
        assertEquals(BigInteger.valueOf(335), blindLevels.get(7));
        assertEquals(BigInteger.valueOf(500), blindLevels.get(8));
    }

    @Test
    void Should_NotSetBlindsSmallerThanFiveDollars() {
        int numOfPlayers = 5;
        BigInteger buyIn = BigInteger.valueOf(20);
        int totalTournamentTime = 180;
        int timeToRaiseBlinds = 20;

        List<BigInteger> blindLevels = BlindHelper.calculateBlindLevels(
                numOfPlayers, buyIn, totalTournamentTime, timeToRaiseBlinds
        );

        assertEquals(BigInteger.valueOf(5), blindLevels.get(0));
    }
}