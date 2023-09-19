package de.thm.holdem.model.game;

import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.settings.PokerGameSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PokerGameTest {

    private PokerPlayer creator;
    private PokerGameSettings settings;
    private PokerGame pokerGame;

    @BeforeEach
    void setUp() {
        creator = new PokerPlayer("Creator");
        settings = new PokerGameSettings(5, 20, 3, 180);
        pokerGame = new PokerGame(creator, 1000, settings);
    }

    @Test
    void Should_ThrowException_If_TooManyPlayersJoin() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Player1"));
        pokerGame.addPlayer(new PokerPlayer("Player2"));
        pokerGame.addPlayer(new PokerPlayer("Player3"));
        pokerGame.addPlayer(new PokerPlayer("Player4"));
        assertThrows(Exception.class, () -> pokerGame.addPlayer(new PokerPlayer("Player5")));
    }

    @Test
    void Should_StartGame_If_GameIsFull() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Player1"));
        pokerGame.addPlayer(new PokerPlayer("Player2"));
        pokerGame.addPlayer(new PokerPlayer("Player3"));
        pokerGame.addPlayer(new PokerPlayer("Player4"));

        assertEquals(GameStatus.IN_PROGRESS, pokerGame.getGameStatus());
    }

    @Test
    void Should_NotStartGame_If_LessThanThreePlayers() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Player1"));
        assertThrows(Exception.class, () -> pokerGame.startGame());
        assertNotEquals(GameStatus.IN_PROGRESS, pokerGame.getGameStatus());
    }

    @Test
    void Should_NotStartGame_If_GameIsAlreadyInProgress() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Player1"));
        pokerGame.addPlayer(new PokerPlayer("Player2"));
        pokerGame.startGame();
        assertThrows(Exception.class, () -> pokerGame.startGame());
    }

    @Test
    void Should_StartGame() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));
        pokerGame.startGame();

        assertEquals(GameStatus.IN_PROGRESS, pokerGame.getGameStatus());
        assertEquals("Creator", pokerGame.getDealer().getAlias());
        assertEquals("Small Blind", pokerGame.getSmallBlindPlayer().getAlias());
        assertEquals("Big Blind", pokerGame.getBigBlindPlayer().getAlias());
    }

    @Test
    void Should_ThrowException_If_DealIsCalledAndGameIsNotInProgress() {
        assertThrows(Exception.class, () -> pokerGame.deal());
    }

    @Test
    public void Should_DealTwoCardsToEachPlayer() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));
        pokerGame.startGame();
        pokerGame.deal();


        // Verify that each player received two cards
        assertEquals(2, pokerGame.getSmallBlindPlayer().getHand().size());
        assertEquals(2, pokerGame.getBigBlindPlayer().getHand().size());
        assertEquals(2, pokerGame.getDealer().getHand().size());
    }

    @Test
    void Should_BetBlinds_If_DealIsCalled() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));
        pokerGame.startGame();
        pokerGame.deal();

        // mock blind levels
        pokerGame.setSmallBlindLevels(Arrays.asList(10, 20, 30));

        // Verify that small blind and big blind bets were placed correctly
        assertEquals(10, pokerGame.getSmallBlindPlayer().getCurrentBet());
        assertEquals(20, pokerGame.getBigBlindPlayer().getCurrentBet());

        // Verify that current bet is set to big blind
        assertEquals(20, pokerGame.getCurrentBet());
    }

    @Test
    void Should_SetActivePlayer_If_DealIsCalled() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));
        pokerGame.startGame();
        pokerGame.deal();

        // Verify that the active player is set correctly (should be the next player after big blind)
        assertEquals(pokerGame.getDealer(), pokerGame.getActivePlayer());
    }

    @Test
    void Should_RaiseBlindLevel() {
        pokerGame.raiseBlinds();
        assertEquals(1, pokerGame.getCurrentBlindLevel());
    }

    @Test
    public void Should_GetNextActivePlayer_If_NotFoldedOrAllIn() throws Exception {
        pokerGame.setState(PokerGame.State.PRE_FLOP);
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));

        // Set the active player to the first player (Creator)
        PokerPlayer currentPlayer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        pokerGame.setActivePlayer(currentPlayer);


        pokerGame.getNextTurn(currentPlayer);

        // Verify that the next eligible player is selected (Player 2)
        assertEquals(pokerGame.getPlayerList().get(1), pokerGame.getActivePlayer());
    }

    @Test
    public void Should_GetNextActivePlayer_If_NextPlayerIsFolded() throws Exception {
        pokerGame.setState(PokerGame.State.PRE_FLOP);
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));

        // Set the active player to the first player (Player 1)
        PokerPlayer currentPlayer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        pokerGame.setActivePlayer(currentPlayer);

        // fold next player
        ((PokerPlayer) pokerGame.getPlayerList().get(1)).fold();

        pokerGame.getNextTurn(currentPlayer);

        // Verify that the next eligible player is selected (Player 2)
        assertEquals(pokerGame.getPlayerList().get(2), pokerGame.getActivePlayer());
    }

    @Test
    public void Should_KeepActivePlayer_If_NoEligiblePlayerLeft() throws Exception {
        pokerGame.setState(PokerGame.State.PRE_FLOP);
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));

        // Set the active player to the first player (Player 1)
        PokerPlayer currentPlayer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        pokerGame.setActivePlayer(currentPlayer);

        // Mark all players as all-in except the current player
        pokerGame.getPlayerList().forEach(player -> {
            if (player != currentPlayer) {
                ((PokerPlayer) player).isFolded(true);
            }
        });

        pokerGame.getNextTurn(currentPlayer);

        // Verify that there are no eligible players left, so the active player remains the same
        assertEquals(currentPlayer, pokerGame.getActivePlayer());
    }

    // NEXT: setNextBlinds

}