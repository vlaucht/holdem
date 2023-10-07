package de.thm.holdem.model.game.poker;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.model.game.GameStatus;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.settings.PokerGameSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PokerGameTest {

    private PokerGame pokerGame;

    @Mock
    private PokerPlayer creator;

    @Mock
    private PokerPlayer player1;

    @Mock
    private PokerPlayer player2;

    @Mock
    private PokerPlayer player3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PokerGameSettings settings = new PokerGameSettings();
        settings.setTimePerPlayerMove(3);
        settings.setTimeToRaiseBlinds(20);
        settings.setTotalTournamentTime(180);
        pokerGame = new PokerGame(creator, BigInteger.valueOf(1000), settings, TableType.NO_LIMIT, 3, "test::game");
    }

    @Test
    void Should_GetPlayerById() {
        when(creator.getId()).thenReturn("test::id");

        PokerPlayer result = (PokerPlayer) pokerGame.getPlayerById("test::id");

        assertEquals(creator, result);
    }

    @Test
    void Should_RemovePlayerFromGame() {
        pokerGame.removePlayer(creator);

        assertEquals(0, pokerGame.getPlayerList().size());
    }

    @Test
    void Should_ThrowException_If_TooManyPlayersJoin() throws Exception {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        doNothing().when(mockPokerGame).startGame();
        mockPokerGame.addPlayer(player1);
        mockPokerGame.addPlayer(player2);

        assertThrows(GameActionException.class, () -> mockPokerGame.addPlayer(player3));
    }

    @Test
    void Should_ThrowException_If_PlayerJoinsTwice() throws Exception {
        pokerGame.addPlayer(player1);
        assertThrows(GameActionException.class, () -> pokerGame.addPlayer(player1));
    }

    @Test
    void Should_StartGame_If_MaxPlayerCountReached() throws Exception {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        doNothing().when(mockPokerGame).startGame();
        mockPokerGame.addPlayer(player1);
        mockPokerGame.addPlayer(player2);

        verify(mockPokerGame, times(1)).startGame();
    }

    @Test
    void Should_NotStartGame_If_GameIsAlreadyRunning() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        when(mockPokerGame.getGameStatus()).thenReturn(GameStatus.IN_PROGRESS);

        assertThrows(GameActionException.class, mockPokerGame::startGame);
    }

    @Test
    void Should_NotStartGame_If_GameIsAlreadyFinished() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        when(mockPokerGame.getGameStatus()).thenReturn(GameStatus.FINISHED);

        assertThrows(GameActionException.class, mockPokerGame::startGame);
    }

    @Test
    void Should_NotStartGame_If_LessThanTwoPlayersJoined() {
        assertThrows(GameActionException.class, pokerGame::startGame);
    }

    @Test
    void Should_StartGame() throws Exception {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        doNothing().when(mockPokerGame).deal();
        mockPokerGame.addPlayer(player1);

        mockPokerGame.startGame();

        assertEquals(GameStatus.IN_PROGRESS, mockPokerGame.getGameStatus());
        assertNotNull(mockPokerGame.dealer);
        assertEquals(mockPokerGame.dealer, mockPokerGame.actor);
    }

    @Test
    void Should_NotDeal_If_GameHasNotStarted() {
        assertThrows(GameActionException.class, pokerGame::deal);
    }

    @Test
    void Should_DealTwoCardsToPlayers  () throws Exception {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        doNothing().when(mockPokerGame).startGame();
        mockPokerGame.addPlayer(player1);
        mockPokerGame.addPlayer(player2);
        PokerHand mockHand = Mockito.mock(PokerHand.class);
        when(mockPokerGame.getGameStatus()).thenReturn(GameStatus.IN_PROGRESS);
        doNothing().when(mockPokerGame).paySmallBlind();
        doNothing().when(mockPokerGame).payBigBlind();
        doNothing().when(mockPokerGame).notifyGameState(ClientOperation.DEAL);
        doNothing().when(mockPokerGame).notifyPlayers(ClientOperation.DEAL);
        doNothing().when(creator).dealCard(any());
        doNothing().when(player1).dealCard(any());
        when(creator.getHand()).thenReturn(mockHand);
        when(player1.getHand()).thenReturn(mockHand);

        // verify that player2 is not dealt cards because he is a spectator
        when(player2.isSpectator()).thenReturn(true);


        mockPokerGame.deck = Mockito.spy(mockPokerGame.deck);

        mockPokerGame.deal();

        assertEquals(BettingRound.PRE_FLOP, mockPokerGame.bettingRound);
        verify(mockPokerGame.deck, times(1)).shuffle();
        verify(mockPokerGame, times(1)).paySmallBlind();
        verify(mockPokerGame, times(1)).payBigBlind();
        verify(mockPokerGame, times(1)).notifyGameState(ClientOperation.DEAL);
        verify(mockPokerGame, times(1)).notifyPlayers(ClientOperation.DEAL);
        verify(creator, times(2)).dealCard(any());
        verify(player1, times(2)).dealCard(any());
        verify(player2, times(0)).dealCard(any());
        verify(mockHand, times(2)).addCards(any());
    }

    @Test
    void Should_PaySmallBlind() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBlindLevel = 0;
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});
        doNothing().when(creator).paySmallBlind(BigInteger.valueOf(10));
        doNothing().when(mockPokerGame).rotateActor(false);
        doNothing().when(mockPokerGame).contributePot(BigInteger.valueOf(10));

        mockPokerGame.paySmallBlind();

        verify(mockPokerGame, times(1)).rotateActor(false);
        assertEquals(creator, mockPokerGame.smallBlindPlayer);
        verify(creator, times(1)).paySmallBlind(BigInteger.valueOf(10));
        verify(mockPokerGame, times(1)).contributePot(BigInteger.valueOf(10));
        assertEquals(BigInteger.valueOf(10), mockPokerGame.currentBet);
    }

    @Test
    void Should_PayBigBlind() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBlindLevel = 0;
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});
        doNothing().when(creator).paySmallBlind(BigInteger.valueOf(20));
        doNothing().when(mockPokerGame).rotateActor(true);
        doNothing().when(mockPokerGame).contributePot(BigInteger.valueOf(20));

        mockPokerGame.payBigBlind();

        verify(mockPokerGame, times(1)).rotateActor(true);
        assertEquals(creator, mockPokerGame.bigBlindPlayer);
        verify(creator, times(1)).payBigBlind(BigInteger.valueOf(20));
        verify(mockPokerGame, times(1)).contributePot(BigInteger.valueOf(20));
        assertEquals(BigInteger.valueOf(20), mockPokerGame.currentBet);
    }

    @Test
    void Should_ContributeToPot() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.pots.clear();

        mockPokerGame.contributePot(BigInteger.valueOf(10));
        assertEquals(1, mockPokerGame.pots.size());
        assertEquals(BigInteger.valueOf(10), mockPokerGame.pots.get(0).getPotSize());
    }


    @Test
    void Should_NotCreateSplitPot_If_NoPlayerIsPartialAllIn() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        Pot mockPot = Mockito.mock(Pot.class);
        when(mockPot.getAllInPlayerWithSmallestStack()).thenReturn(null);
        mockPokerGame.pots.add(mockPot);

        assertEquals(1, mockPokerGame.pots.size());
        mockPokerGame.checkForSplitPots();
        assertEquals(1, mockPokerGame.pots.size());
    }

    @Test
    void Should_CreateSplitPot_If_PlayerIsPartialAllIn() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        Pot mockPot = Mockito.mock(Pot.class);
        Pot mockSplitPot = Mockito.mock(Pot.class);
        when(mockPot.getAllInPlayerWithSmallestStack()).thenReturn(player1);
        when(mockSplitPot.getAllInPlayerWithSmallestStack()).thenReturn(null);
        when(mockPot.split(player1)).thenReturn(mockSplitPot);

        mockPokerGame.pots.add(mockPot);

        assertEquals(1, mockPokerGame.pots.size());
        mockPokerGame.checkForSplitPots();
        assertEquals(2, mockPokerGame.pots.size());
    }

    @Test
    void Should_CreateMultipleSplitPots() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        Pot mockPot = Mockito.mock(Pot.class);
        Pot mockSplitPot = Mockito.mock(Pot.class);
        Pot mockSplitPot2 = Mockito.mock(Pot.class);
        when(mockPot.getAllInPlayerWithSmallestStack()).thenReturn(player1);
        when(mockSplitPot.getAllInPlayerWithSmallestStack()).thenReturn(player2);
        when(mockSplitPot2.getAllInPlayerWithSmallestStack()).thenReturn(null);
        when(mockPot.split(player1)).thenReturn(mockSplitPot);
        when(mockSplitPot.split(player2)).thenReturn(mockSplitPot2);

        mockPokerGame.pots.add(mockPot);

        assertEquals(1, mockPokerGame.pots.size());
        mockPokerGame.checkForSplitPots();
        assertEquals(3, mockPokerGame.pots.size());
    }

    @Test
    void Should_ThrowException_If_BlindIsRaisedDuringRound() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.bettingRound = BettingRound.PRE_FLOP;
        assertThrows(GameActionException.class, mockPokerGame::raiseBlinds);
    }

    @Test
    void Should_RaiseBlindLevel_If_BlindLimitNotReached() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.bettingRound = BettingRound.NONE;
        mockPokerGame.currentBlindLevel = 0;
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});
        mockPokerGame.raiseBlinds();

        assertEquals(1, mockPokerGame.currentBlindLevel);
    }

    @Test
    void Should_NotRaiseBlindLevel_If_BlindLimitReached() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.bettingRound = BettingRound.NONE;
        mockPokerGame.currentBlindLevel = 1;
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});
        mockPokerGame.raiseBlinds();

        assertEquals(1, mockPokerGame.currentBlindLevel);
    }

    @Test
    void Should_RotateActorToNextPlayer() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.bettingRound = BettingRound.PRE_FLOP;
        mockPokerGame.actor = creator;
        mockPokerGame.getPlayerList().add(player1);
        when(player1.isFolded()).thenReturn(false);
        when(player1.isSpectator()).thenReturn(false);
        when(player1.isAllIn()).thenReturn(false);

        mockPokerGame.rotateActor(false);

        assertEquals(player1, mockPokerGame.actor);

    }

    @Test
    void Should_SkipPlayerOnRotation_If_PlayerIsFolded() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.bettingRound = BettingRound.PRE_FLOP;
        mockPokerGame.actor = creator;
        mockPokerGame.getPlayerList().add(player1);
        mockPokerGame.getPlayerList().add(player2);
        when(player1.isFolded()).thenReturn(true);
        when(player1.isSpectator()).thenReturn(false);
        when(player1.isAllIn()).thenReturn(false);
        when(player2.isFolded()).thenReturn(false);
        when(player2.isSpectator()).thenReturn(false);
        when(player2.isAllIn()).thenReturn(false);

        mockPokerGame.rotateActor(false);

        assertEquals(player2, mockPokerGame.actor);
    }

    @Test
    void Should_ResetAllowedActionsOnRotation() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.bettingRound = BettingRound.PRE_FLOP;
        creator.addAllowedAction(PokerPlayerAction.CHECK);
        mockPokerGame.actor = creator;
        mockPokerGame.getPlayerList().add(player1);
        when(player1.isFolded()).thenReturn(false);
        when(player1.isSpectator()).thenReturn(false);
        when(player1.isAllIn()).thenReturn(false);

        mockPokerGame.rotateActor(false);

        assertEquals(0, creator.getAllowedActions().size());
    }

    @Test
    void Should_SetAllowedActionsOnRotation_If_Requested() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.bettingRound = BettingRound.PRE_FLOP;
        mockPokerGame.actor = creator;
        mockPokerGame.getPlayerList().add(player1);
        when(player1.isFolded()).thenReturn(false);
        when(player1.isSpectator()).thenReturn(false);
        when(player1.isAllIn()).thenReturn(false);
        doNothing().when(mockPokerGame).setAllowedActions();

        mockPokerGame.rotateActor(true);

        verify(mockPokerGame, times(1)).setAllowedActions();
    }

    @Test
    void Should_AutomaticallyCheck_If_ActorIsAllIn() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        when(creator.getLastAction()).thenReturn(PokerPlayerAction.ALL_IN);
        doNothing().when(mockPokerGame).check(creator);
        doNothing().when(mockPokerGame).notifyPlayers(ClientOperation.PLAYER_ACTION);

        mockPokerGame.setAllowedActions();

        verify(mockPokerGame, times(1)).check(creator);
        verify(mockPokerGame, times(1)).notifyPlayers(ClientOperation.PLAYER_ACTION);
        assertEquals(0, creator.getAllowedActions().size());
    }

    @Test
    void Should_OnlyAddFoldAndAllIn_If_PlayerHasFewerChipsThanCurrentBet() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});

        when(creator.getChips()).thenReturn(BigInteger.valueOf(100));
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(0));
        when(creator.getLastAction()).thenReturn(null);

        mockPokerGame.setAllowedActions();

        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.FOLD);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.ALL_IN);
        verify(creator, times(2)).addAllowedAction(any());
    }

    @Test
    void Should_AllowCheck_If_PlayerHasMatchedCurrentBet() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});

        when(creator.getChips()).thenReturn(BigInteger.valueOf(0));
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(200));
        when(creator.getLastAction()).thenReturn(null);

        mockPokerGame.setAllowedActions();

        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.CHECK);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.FOLD);
        verify(creator, times(2)).addAllowedAction(any());
    }

    @Test
    void Should_AllowCheckAndAllIn_If_PlayerHasMatchedCurrentBetAndHasFewerChipsThanBigBlind() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});

        when(creator.getChips()).thenReturn(BigInteger.valueOf(10));
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(200));
        when(creator.getLastAction()).thenReturn(null);

        mockPokerGame.setAllowedActions();

        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.CHECK);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.FOLD);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.ALL_IN);
        verify(creator, times(3)).addAllowedAction(any());
    }

    @Test
    void Should_AllowCall_If_PlayerHasNotMatchedCurrentBetAndHasEnoughChipsToCall() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});

        when(creator.getChips()).thenReturn(BigInteger.valueOf(100));
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(100));
        when(creator.getLastAction()).thenReturn(null);

        mockPokerGame.setAllowedActions();

        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.CALL);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.FOLD);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.ALL_IN);
        verify(creator, times(3)).addAllowedAction(any());
    }

    @Test
    void Should_AllowRaise_If_PlayerHasMoreChipsThanHeNeedsToCallPlusBigBlind() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});

        when(creator.getChips()).thenReturn(BigInteger.valueOf(300));
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(100));
        when(creator.getLastAction()).thenReturn(null);

        mockPokerGame.setAllowedActions();

        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.CALL);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.FOLD);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.RAISE);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.ALL_IN);
        verify(creator, times(4)).addAllowedAction(any());
    }

    @Test
    void Should_NotAllowRaise_If_TableHasLimitAndLimitIsReached() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});

        when(mockPokerGame.getTableType()).thenReturn(TableType.FIXED_LIMIT);
        when(mockPokerGame.getRaises()).thenReturn(3);
        when(creator.getChips()).thenReturn(BigInteger.valueOf(300));
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(100));
        when(creator.getLastAction()).thenReturn(null);

        mockPokerGame.setAllowedActions();

        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.CALL);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.FOLD);
        verify(creator, times(1)).addAllowedAction(PokerPlayerAction.ALL_IN);
        verify(creator, times(3)).addAllowedAction(any());
    }

    @Test
    void Should_SetNextDealer() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.getPlayerList().add(player1);
        mockPokerGame.dealer = creator;
        mockPokerGame.setNextDealer();

        assertEquals(player1, mockPokerGame.dealer);
        assertEquals(player1, mockPokerGame.actor);

    }



}
