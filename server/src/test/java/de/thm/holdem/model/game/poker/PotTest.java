package de.thm.holdem.model.game.poker;

import de.thm.holdem.model.player.PokerPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PotTest {
    private Pot pot;

    @Mock
    private PokerPlayer player1;

    @Mock
    private PokerPlayer player2;

    @Mock
    private PokerPlayer player3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pot = new Pot();
    }

    @Test
    void Should_ReturnPlayerWithTheHighestHandScoreAsWinner() {
        // Configure mock behavior for getHandScore method
        when(player1.getHandScore()).thenReturn(100);
        when(player2.getHandScore()).thenReturn(50);

        // Add mock players to the contributors
        pot.addContributor(player1, BigInteger.valueOf(200));
        pot.addContributor(player2, BigInteger.valueOf(200));

        Set<PokerPlayer> winners = pot.getWinners();

        // player 1 has higher hand score
        Set<PokerPlayer> expectedWinners = new HashSet<>();
        expectedWinners.add(player1);

        assertEquals(expectedWinners, winners);
    }

    @Test
    void Should_IgnorePlayersThatHaveFolded() {
        // Configure mock behavior for getHandScore method
        when(player1.getHandScore()).thenReturn(100);
        when(player2.getHandScore()).thenReturn(50);
        when(player1.isFolded()).thenReturn(true);

        // Add mock players to the contributors
        pot.addContributor(player1, BigInteger.valueOf(200));
        pot.addContributor(player2, BigInteger.valueOf(200));


        Set<PokerPlayer> winners = pot.getWinners();

        // player 1 has higher hand score but has folded
        Set<PokerPlayer> expectedWinners = new HashSet<>();
        expectedWinners.add(player2);

        assertEquals(expectedWinners, winners);

    }

    @Test
    void Should_ReturnAllWinners_If_ThereIsATie() {
        // Configure mock behavior for getHandScore method
        when(player1.getHandScore()).thenReturn(100);
        when(player2.getHandScore()).thenReturn(100);

        // Add mock players to the contributors
        pot.addContributor(player1, BigInteger.valueOf(200));
        pot.addContributor(player2, BigInteger.valueOf(200));

        Set<PokerPlayer> winners = pot.getWinners();

        // player 1 and player 2 have the same hand score
        Set<PokerPlayer> expectedWinners = new HashSet<>();
        expectedWinners.add(player1);
        expectedWinners.add(player2);

        assertEquals(expectedWinners, winners);

    }

    @Test
    void Should_ClearPot() {
        pot.addContributor(player1, BigInteger.valueOf(100));

        pot.clear();

        assertEquals(0, pot.getContributors().size());
        assertEquals(BigInteger.ZERO, pot.getPotSize());
    }


    @Test
    void Should_AddContributorToPot_If_HeDoesntExistYet() {

        pot.contribute(player1, BigInteger.valueOf(100));

        assertEquals(BigInteger.valueOf(100), pot.getPlayerContribution(player1));
    }

    @Test
    void Should_AddToContribution_If_PlayerExists() {
        pot.contribute(player1, BigInteger.valueOf(100));
        pot.contribute(player1, BigInteger.valueOf(100));

        assertEquals(BigInteger.valueOf(200), pot.getPlayerContribution(player1));
    }

    @Test
    void Should_GetTotalPotSize() {
        pot.addContributor(player1, BigInteger.valueOf(100));
        pot.addContributor(player2, BigInteger.valueOf(200));

        assertEquals(BigInteger.valueOf(300), pot.getPotSize());
    }

    @Test
    void Should_GetAllInPlayerWithSmallestStack() {
        when(player1.isAllIn()).thenReturn(true);
        when(player2.isAllIn()).thenReturn(true);


        pot.addContributor(player1, BigInteger.valueOf(100));
        pot.addContributor(player2, BigInteger.valueOf(50)); // Smallest stack

        PokerPlayer allInPlayer = pot.getAllInPlayerWithSmallestStack();

        assertEquals(player2, allInPlayer);
    }

    @Test
    void Should_ReturnNull_If_NoPlayerIsAllIn() {
        when(player1.isAllIn()).thenReturn(false);
        when(player2.isAllIn()).thenReturn(false);

        pot.addContributor(player1, BigInteger.valueOf(100));
        pot.addContributor(player2, BigInteger.valueOf(50));

        PokerPlayer allInPlayer = pot.getAllInPlayerWithSmallestStack();

        assertNull(allInPlayer);
    }

    @Test
    void Should_SplitPot() {

        pot.addContributor(player1, BigInteger.valueOf(100));
        pot.addContributor(player2, BigInteger.valueOf(200));
        pot.addContributor(player3, BigInteger.valueOf(200));

        Pot sidePot = pot.split(player1);

        // the old pot should have the allInPlayer's contribution from each player
        assertEquals(BigInteger.valueOf(100), pot.getPlayerContribution(player1));
        assertEquals(BigInteger.valueOf(100), pot.getPlayerContribution(player2));
        assertEquals(BigInteger.valueOf(100), pot.getPlayerContribution(player3));
        assertEquals(BigInteger.valueOf(300), pot.getPotSize());

        // the side pot should have the remaining contribution from each player and the allInPlayer should not be a contributor
        assertFalse(sidePot.contributions.containsKey(player1));
        assertEquals(BigInteger.valueOf(100), sidePot.getPlayerContribution(player2));
        assertEquals(BigInteger.valueOf(100), sidePot.getPlayerContribution(player3));
        assertEquals(BigInteger.valueOf(200), sidePot.getPotSize());
    }
}