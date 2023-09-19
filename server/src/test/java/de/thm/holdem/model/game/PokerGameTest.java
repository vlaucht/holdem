package de.thm.holdem.model.game;

import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.settings.PokerGameSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        pokerGame.startGame();

        assertNotEquals(GameStatus.IN_PROGRESS, pokerGame.getGameStatus());
    }

}