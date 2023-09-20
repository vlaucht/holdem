package de.thm.holdem.model.game;

import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.exception.IllegalGameActionException;
import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Rank;
import de.thm.holdem.model.card.Suit;
import de.thm.holdem.model.player.Player;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.service.PokerHandEvaluator;
import de.thm.holdem.settings.PokerGameSettings;
import de.thm.holdem.utils.ClassFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PokerGameTest {

    private PokerGame pokerGame;
    private PokerPlayer creator;

    private PokerGameSettings settings;

    @BeforeEach
    void setUp() {
        creator  = new PokerPlayer("Creator");
        settings = new PokerGameSettings();
        settings.setMaxPlayers(5);
        settings.setTimeToRaiseBlinds(20);
        settings.setTimePerPlayerMove(3);
        settings.setTotalTournamentTime(180);
        pokerGame = new PokerGame(creator, 1000, settings);
    }

    @Test
    void Should_DeductBuyInFromCreator() {
        creator  = new PokerPlayer("Creator");
        int bankRoll = creator.getBankroll();
        pokerGame = new PokerGame(creator, 1000, settings);

        assertEquals(bankRoll - pokerGame.getBuyIn(), pokerGame.getPlayerList().get(0).getBankroll());
    }

    @Test
    void Should_ThrowException_If_TooManyPlayersJoin() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Player1"));
        pokerGame.addPlayer(new PokerPlayer("Player2"));
        pokerGame.addPlayer(new PokerPlayer("Player3"));
        pokerGame.addPlayer(new PokerPlayer("Player4"));
        assertThrows(IllegalGameActionException.class, () -> pokerGame.addPlayer(new PokerPlayer("Player5")));
    }

    @Test
    void Should_DeductBuyInFromJoiningPlayers() throws Exception {
        PokerPlayer player = new PokerPlayer("Player1");
        int bankroll = player.getBankroll();
        pokerGame.addPlayer(player);
        pokerGame.addPlayer(new PokerPlayer("Player2"));
        pokerGame.getPlayerList().forEach(p -> {
            assertEquals(bankroll - pokerGame.getBuyIn(), p.getBankroll());
        });
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
        assertThrows(GameActionException.class, () -> pokerGame.startGame());
        assertNotEquals(GameStatus.IN_PROGRESS, pokerGame.getGameStatus());
    }

    @Test
    void Should_NotStartGame_If_GameIsAlreadyInProgress() throws Exception {
        pokerGame.addPlayer(new PokerPlayer("Player1"));
        pokerGame.addPlayer(new PokerPlayer("Player2"));
        pokerGame.startGame();
        assertThrows(IllegalGameActionException.class, () -> pokerGame.startGame());
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
        assertThrows(IllegalGameActionException.class, () -> pokerGame.deal());
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
        pokerGame.smallBlindLevels = Arrays.asList(10, 20, 30);

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
        pokerGame.state = PokerGame.State.PRE_FLOP;
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));

        // Set the active player to the first player (Creator)
        PokerPlayer currentPlayer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        pokerGame.activePlayer = currentPlayer;


        pokerGame.setNextActivePlayer(currentPlayer);

        // Verify that the next eligible player is selected (Player 2)
        assertEquals(pokerGame.getPlayerList().get(1), pokerGame.getActivePlayer());
    }

    @Test
    public void Should_GetNextActivePlayer_If_NextPlayerIsFolded() throws Exception {
        pokerGame.state = PokerGame.State.PRE_FLOP;
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));

        // Set the active player to the first player (Player 1)
        PokerPlayer currentPlayer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        pokerGame.activePlayer = currentPlayer;

        // fold next player
        ((PokerPlayer) pokerGame.getPlayerList().get(1)).fold();

        pokerGame.setNextActivePlayer(currentPlayer);

        // Verify that the next eligible player is selected (Player 2)
        assertEquals(pokerGame.getPlayerList().get(2), pokerGame.getActivePlayer());
    }

    @Test
    public void Should_KeepActivePlayer_If_NoEligiblePlayerLeft() throws Exception {
        pokerGame.state = PokerGame.State.PRE_FLOP;
        pokerGame.addPlayer(new PokerPlayer("Small Blind"));
        pokerGame.addPlayer(new PokerPlayer("Big Blind"));

        // Set the active player to the first player (Player 1)
        PokerPlayer currentPlayer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        pokerGame.activePlayer = currentPlayer;

        // Mark all players as folded except the current player
        pokerGame.getPlayerList().forEach(player -> {
            if (player != currentPlayer) {
                ((PokerPlayer) player).fold();
            }
        });

        pokerGame.setNextActivePlayer(currentPlayer);

        // Verify that there are no eligible players left, so the active player remains the same
        assertEquals(currentPlayer, pokerGame.getActivePlayer());
    }

    @Test
    void Should_SetTheBlindsToNextEligiblePlayer() throws Exception {
        PokerPlayer smallBlind = new PokerPlayer("Small Blind");
        PokerPlayer bigBlind = new PokerPlayer("Big Blind");
        pokerGame.addPlayer(smallBlind);
        pokerGame.addPlayer(bigBlind);
        pokerGame.setNextBlinds(smallBlind);

        // Small blind should move to big blind
        assertEquals(bigBlind, pokerGame.getSmallBlindPlayer());

        // Big Blind should move to dealer (if only 3 players in the game)
        assertEquals(creator, pokerGame.getBigBlindPlayer());
    }

    @Test
    void Should_SkipPlayersWithoutChips_If_BlindsAreSet() throws Exception {
        PokerPlayer smallBlind = new PokerPlayer("Small Blind");
        PokerPlayer bigBlind = new PokerPlayer("Big Blind");
        pokerGame.addPlayer(smallBlind);
        pokerGame.addPlayer(bigBlind);
        pokerGame.addPlayer(new PokerPlayer("Player 3"));
        pokerGame.getPlayerList().get(2).setChips(0);
        pokerGame.setNextBlinds(smallBlind);

        // Small blind should move to Player2 since big blind has no chips
        assertEquals(pokerGame.getPlayerList().get(3), pokerGame.getSmallBlindPlayer());

        // Big Blind should move to dealer (if only 3 players in the game)
        assertEquals(creator, pokerGame.getBigBlindPlayer());
    }

    @Test
    void Should_SetBigBlindToDealer_If_OnlyTwoPlayersLeft() throws Exception {
        PokerPlayer smallBlind = new PokerPlayer("Small Blind");
        PokerPlayer bigBlind = new PokerPlayer("Big Blind");
        pokerGame.addPlayer(smallBlind);
        pokerGame.addPlayer(bigBlind);
        pokerGame.startGame();
        pokerGame.getPlayerList().get(1).setChips(0);
        pokerGame.flopCards = new ArrayList<>();
        pokerGame.prepareNextRound();

        assertEquals(creator, pokerGame.getBigBlindPlayer());
        assertEquals(bigBlind, pokerGame.getSmallBlindPlayer());
        assertEquals(bigBlind, pokerGame.getDealer());
    }

    @Test
    void Should_NotAllowCall_If_NotPlayersTurn() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = pokerGame.getDealer();
        assertThrows(IllegalGameActionException.class, () -> pokerGame.call(player1));
    }

    @Test
    void Should_NotAllowCall_If_PlayerHasMatchedBet() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = player1;
        pokerGame.currentBet = 100;
        player1.setCurrentBet(100);
        assertThrows(GameActionException.class, () -> pokerGame.call(player1));
    }

    @Test
    void Should_NotAllowCall_If_PlayerHasNoChipsToMatchBet() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = player1;
        pokerGame.currentBet = 100;
        player1.setChips(90);
        assertThrows(GameActionException.class, () -> pokerGame.call(player1));
    }

    @Test
    void Should_AllowCall() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = player1;
        pokerGame.currentBet = 100;
        player1.setChips(200);
        pokerGame.call(player1);

        assertEquals(100, player1.getCurrentBet());
        assertEquals(100, player1.getChips());
        assertEquals(100, pokerGame.getCurrentBet());
        assertEquals(PokerPlayerAction.CALL, player1.getLastAction());
    }

    @Test
    void Should_NotAllowCheck_If_NotPlayersTurn() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = pokerGame.getDealer();
        assertThrows(IllegalGameActionException.class, () -> pokerGame.check(player1));
    }

    @Test
    void Should_NotAllowCheck_If_PlayerHasNotMatchedBet() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = player1;
        pokerGame.currentBet = 100;
        player1.setCurrentBet(90);
        assertThrows(GameActionException.class, () -> pokerGame.check(player1));
    }

    @Test
    void Should_AllowCheck() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = player1;
        pokerGame.currentBet = 100;
        player1.setCurrentBet(100);
        pokerGame.check(player1);

        assertEquals(PokerPlayerAction.CHECK, player1.getLastAction());
    }

    @Test
    void Should_NotAllowRaise_If_NotPlayersTurn() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = pokerGame.getDealer();
        assertThrows(IllegalGameActionException.class, () -> pokerGame.raise(player1, 100));
    }

    @Test
    void Should_NotAllowRaise_If_RaiseLowerThatCurrentBetPlusSmallBlind() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = player1;
        pokerGame.smallBlindLevels = Arrays.asList(10, 20, 30);
        pokerGame.currentBet = 100;
        assertThrows(GameActionException.class, () -> pokerGame.raise(player1, 100));
    }

    @Test
    void Should_NotAllowRaise_If_PlayerHasNotEnoughChips() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = player1;
        pokerGame.smallBlindLevels = Arrays.asList(10, 20, 30);
        pokerGame.currentBet = 100;
        assertThrows(GameActionException.class, () -> pokerGame.raise(player1, 2000));
    }

    @Test
    void Should_AllowRaise() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = player1;
        pokerGame.currentBet = 100;
        pokerGame.smallBlindLevels = Arrays.asList(10, 20, 30);
        player1.setChips(910);
        player1.setCurrentBet(90);
        pokerGame.raise(player1, 50);

        assertEquals(PokerPlayerAction.RAISE, player1.getLastAction());
        assertEquals(150, pokerGame.getCurrentBet());
        assertEquals(850, player1.getChips());
    }

    @Test
    void Should_NotAllowFold_If_NotPlayersTurn() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = pokerGame.getDealer();
        assertThrows(IllegalGameActionException.class, () -> pokerGame.fold(player1));
    }

    @Test
    void Should_AllowFold() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.activePlayer = player1;

        pokerGame.fold(player1);
        assertEquals(PokerPlayerAction.FOLD, player1.getLastAction());
        assertTrue(player1.isFolded());
    }

    @Test
    void Should_StartFlopRound_If_StateIsPreFlop() {
        pokerGame.dealer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        pokerGame.state = PokerGame.State.PRE_FLOP;

        pokerGame.startNextRound();

        assertEquals(PokerGame.State.FLOP, pokerGame.getState());
        assertNotNull(pokerGame.getFlopCards());
        // burn 1 card and deal 3 cards
        assertEquals(48, pokerGame.getDeck().size());
        assertEquals(3, pokerGame.getFlopCards().size());
        assertEquals(pokerGame.getDealer(), pokerGame.getActivePlayer());
    }

    @Test
    void Should_StartTurnRound_If_StateIsFlop() {
        pokerGame.dealer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        pokerGame.state = PokerGame.State.FLOP;

        pokerGame.startNextRound();

        assertEquals(PokerGame.State.TURN, pokerGame.getState());
        assertNotNull(pokerGame.getTurnCard());
        // burn 1 card and deal 1 card
        assertEquals(50, pokerGame.getDeck().size());
        assertEquals(pokerGame.getDealer(), pokerGame.getActivePlayer());
    }

    @Test
    void Should_StartRiverRound_If_StateIsTurn() {
        pokerGame.dealer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        pokerGame.state = PokerGame.State.TURN;

        pokerGame.startNextRound();

        assertEquals(PokerGame.State.RIVER, pokerGame.getState());
        assertNotNull(pokerGame.getRiverCard());
        // burn 1 card and deal 1 card
        assertEquals(50, pokerGame.getDeck().size());
        assertEquals(pokerGame.getDealer(), pokerGame.getActivePlayer());
    }

    @Test
    void Should_EndRound_If_StateIsRiver() {
        PokerGame spyGame = Mockito.spy(pokerGame);
        spyGame.dealer = (PokerPlayer) pokerGame.getPlayerList().get(0);
        spyGame.state = PokerGame.State.RIVER;

        doNothing().when(spyGame).evaluateHands();
        spyGame.startNextRound();

        assertEquals(PokerGame.State.END, spyGame.getState());
        verify(spyGame, times(1)).evaluateHands();
    }

    @Test
    void Should_StartNextRound_If_RoundCanEnd() {
        PokerGame spyGame = Mockito.spy(pokerGame);
        doReturn(true).when(spyGame).canRoundEnd();
        doNothing().when(spyGame).startNextRound();
        spyGame.manageRoundEnd();

        verify(spyGame, times(1)).startNextRound();
    }

    @Test
    void Should_SetNextActivePlayer_If_RoundCanNotEnd() {
        PokerGame spyGame = Mockito.spy(pokerGame);
        doReturn(false).when(spyGame).canRoundEnd();
        doNothing().when(spyGame).setNextActivePlayer(any());
        spyGame.manageRoundEnd();

        verify(spyGame, times(1)).setNextActivePlayer(any());
    }

    @Test
    void Should_NotPrepareNextRound_If_LessThanTwoPlayersLeft() throws Exception {
        PokerPlayer player1 = new PokerPlayer("Player 1");
        pokerGame.addPlayer(player1);
        pokerGame.riverCard = new Card(Rank.ACE, Suit.CLUBS);
        player1.setChips(0);

        pokerGame.prepareNextRound();
        assertNotNull(pokerGame.getRiverCard());
        assertEquals(GameStatus.FINISHED, pokerGame.getGameStatus());
    }

    @Test
    void Should_PrepareNextRound() throws Exception {
        PokerGame spyGame = Mockito.spy(pokerGame);
        PokerPlayer player1 = new PokerPlayer("Player 1");
        spyGame.addPlayer(player1);
        spyGame.dealer = (PokerPlayer) spyGame.getPlayerList().get(0);
        spyGame.bigBlindPlayer = spyGame.getDealer();
        spyGame.smallBlindPlayer = player1;
        spyGame.flopCards = new ArrayList<>(List.of(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.KING, Suit.CLUBS),
                new Card(Rank.QUEEN, Suit.CLUBS)));
        spyGame.turnCard = new Card(Rank.JACK, Suit.CLUBS);
        spyGame.riverCard = new Card(Rank.TEN, Suit.CLUBS);
        doNothing().when(spyGame).setNextBlinds(spyGame.smallBlindPlayer);
        doNothing().when(spyGame).setNextDealer(spyGame.dealer);

        spyGame.prepareNextRound();

        verify(spyGame, times(1)).setNextBlinds(spyGame.smallBlindPlayer);
        verify(spyGame, times(1)).setNextDealer(spyGame.dealer);
        assertTrue(spyGame.getFlopCards().isEmpty());
        assertNull(spyGame.getRiverCard());
        assertNull(spyGame.getTurnCard());
        assertEquals(spyGame.smallBlindPlayer, spyGame.getActivePlayer());
    }

    @Test
    void Should_AssignHandScoreToPlayer() throws Exception {
        // Create a mocked factory and hand evaluator
        ClassFactory<PokerHandEvaluator> mockedFactory = mock(ClassFactory.class);
        PokerHandEvaluator handEvaluator = mock(PokerHandEvaluator.class);
        when(handEvaluator.bestHand()).thenReturn(100);
        doReturn(handEvaluator).when(mockedFactory).createInstance(any(), any(), any(), any(), any(), any(), any());

        // Create a player with known hands
        PokerPlayer player1 = new PokerPlayer("Player 1");

        // Create the PokerGame with the mocked factory
        pokerGame = new PokerGame(creator, 1000, settings);
        pokerGame.evaluatorFactory = mockedFactory;
        pokerGame.getPlayerList().remove(0);
        pokerGame.state = PokerGame.State.END;

        // Add the player to the game
        pokerGame.addPlayer(player1);
        player1.setHand(List.of(new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.KING, Suit.DIAMONDS)));

        // Set the flop, turn, and river cards
        pokerGame.flopCards = List.of(new Card(Rank.QUEEN, Suit.CLUBS), new Card(Rank.JACK, Suit.CLUBS), new Card(Rank.NINE, Suit.CLUBS));
        pokerGame.turnCard = new Card(Rank.EIGHT, Suit.DIAMONDS);
        pokerGame.riverCard = new Card(Rank.SEVEN, Suit.HEARTS);

        // Call evaluateHands
        pokerGame.evaluateHands();

        // Assert that the hand scores are calculated correctly
        assertEquals(100, ((PokerPlayer)pokerGame.getPlayerList().get(0)).getHandScore());

    }

    @Test
    void Should_EndRoundAndSetHandScore_If_OnlyOnePlayerNotFolded() throws Exception {
        pokerGame.getPlayerList().remove(0);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        when(player1.isFolded()).thenReturn(true);
        when(player2.isFolded()).thenReturn(true);
        when(player3.isFolded()).thenReturn(false);


        pokerGame.addPlayer(player1);
        pokerGame.addPlayer(player2);
        pokerGame.addPlayer(player3);

        assertTrue(pokerGame.canRoundEnd());

        // Ensure that the round ends and the hand score is set
        assertEquals(PokerGame.State.END, pokerGame.getState());
        verify(player3).setHandScore(1000);
    }

    @Test
    void Should_EndRound_If_StateIsPreFlopAndItsBigBlindTurnAndAllPlayersHaveCalledOrFolded() throws Exception {
        pokerGame.getPlayerList().remove(0);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);

        when(player1.isFolded()).thenReturn(false);
        when(player2.isFolded()).thenReturn(false);
        when(player3.isFolded()).thenReturn(true);

        when(player1.getCurrentBet()).thenReturn(100);
        when(player2.getCurrentBet()).thenReturn(100);
        when(player3.getCurrentBet()).thenReturn(0);


        pokerGame.addPlayer(player1);
        pokerGame.addPlayer(player2);
        pokerGame.addPlayer(player3);

        pokerGame.state = PokerGame.State.PRE_FLOP;
        pokerGame.activePlayer = player1;
        pokerGame.bigBlindPlayer = player1;
        pokerGame.currentBet = 100;

        assertTrue(pokerGame.canRoundEnd());
        assertEquals(PokerGame.State.PRE_FLOP, pokerGame.getState());
    }

    @Test
    void Should_NotEndRound_If_StateIsPreFlopAndItsBigBlindTurnWithRemainingPlayers() throws Exception {
        pokerGame.getPlayerList().remove(0);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        when(player1.getCurrentBet()).thenReturn(150);
        when(player2.getCurrentBet()).thenReturn(100);
        when(player1.isFolded()).thenReturn(false);
        when(player2.isFolded()).thenReturn(false);
        when(player3.isFolded()).thenReturn(true);


        pokerGame.addPlayer(player1);
        pokerGame.addPlayer(player2);
        pokerGame.addPlayer(player3);
        pokerGame.state = PokerGame.State.PRE_FLOP;
        pokerGame.activePlayer = player1;
        pokerGame.bigBlindPlayer = player1;
        pokerGame.currentBet = 100;

        assertFalse(pokerGame.canRoundEnd());
    }

    @Test
    void Should_EndRound_If_StateIsNotPreFlopAndItsDealersTurnAndAllPlayersHaveCalledOrFolded() throws Exception {
        pokerGame.getPlayerList().remove(0);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);

        when(player1.isFolded()).thenReturn(false);
        when(player2.isFolded()).thenReturn(false);
        when(player3.isFolded()).thenReturn(true);

        when(player1.getCurrentBet()).thenReturn(100);
        when(player2.getCurrentBet()).thenReturn(100);
        when(player3.getCurrentBet()).thenReturn(0);


        pokerGame.addPlayer(player1);
        pokerGame.addPlayer(player2);
        pokerGame.addPlayer(player3);

        pokerGame.state = PokerGame.State.TURN;
        pokerGame.activePlayer = player1;
        pokerGame.dealer = player1;
        pokerGame.currentBet = 100;

        assertTrue(pokerGame.canRoundEnd());
        assertEquals(PokerGame.State.TURN, pokerGame.getState());
    }

    @Test
    void Should_NotEndRound_If_StateIsNotPreFlopAndItsDealersTurnWithRemainingPlayers() throws Exception {
        pokerGame.getPlayerList().remove(0);
        PokerPlayer player1 = mock(PokerPlayer.class);
        PokerPlayer player2 = mock(PokerPlayer.class);
        PokerPlayer player3 = mock(PokerPlayer.class);
        when(player1.getCurrentBet()).thenReturn(100);
        when(player2.getCurrentBet()).thenReturn(150);
        when(player1.isFolded()).thenReturn(false);
        when(player2.isFolded()).thenReturn(false);
        when(player3.isFolded()).thenReturn(true);


        pokerGame.addPlayer(player1);
        pokerGame.addPlayer(player2);
        pokerGame.addPlayer(player3);
        pokerGame.state = PokerGame.State.TURN;
        pokerGame.activePlayer = player1;
        pokerGame.dealer = player1;
        pokerGame.currentBet = 100;

        assertFalse(pokerGame.canRoundEnd());
    }



}