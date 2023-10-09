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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
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

        GameActionException exception = assertThrows(GameActionException.class, () -> pokerGame.addPlayer(player1));

        assertThat(
                exception.getMessage(),
                containsString("Player can not join this game.")
        );
    }

    @Test
    void Should_ThrowException_If_PlayerJoinsTwice() throws Exception {
        pokerGame.addPlayer(player1);

        GameActionException exception = assertThrows(GameActionException.class, () -> pokerGame.addPlayer(player1));

        assertThat(
                exception.getMessage(),
                containsString("Player can not join this game.")
        );

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

        GameActionException exception = assertThrows(GameActionException.class, mockPokerGame::startGame);

        assertThat(
                exception.getMessage(),
                containsString("Game is already running.")
        );
    }

    @Test
    void Should_NotStartGame_If_GameIsAlreadyFinished() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        when(mockPokerGame.getGameStatus()).thenReturn(GameStatus.FINISHED);

        GameActionException exception = assertThrows(GameActionException.class, mockPokerGame::startGame);

        assertThat(
                exception.getMessage(),
                containsString("Game is already running.")
        );
    }

    @Test
    void Should_NotStartGame_If_LessThanTwoPlayersJoined() {
        GameActionException exception = assertThrows(GameActionException.class, pokerGame::startGame);

        assertThat(
                exception.getMessage(),
                containsString("Not enough players to start the game.")
        );
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
        GameActionException exception = assertThrows(GameActionException.class, pokerGame::deal);

        assertThat(
                exception.getMessage(),
                containsString("Game has not started yet.")
        );
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

        GameActionException exception = assertThrows(GameActionException.class, mockPokerGame::raiseBlinds);

        assertThat(
                exception.getMessage(),
                containsString("Blinds can not be raised during a round.")
        );
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

    @Test
    void Should_NotAllowAction_If_NotActorsTurn() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.getPlayerList().add(player1);
        mockPokerGame.actor = player1;


        GameActionException exception = assertThrows(GameActionException.class, () ->
                mockPokerGame.isIllegalAction(creator, PokerPlayerAction.CHECK));

        assertThat(
                exception.getMessage(),
                containsString("It is not your turn.")
        );
    }

    @Test
    void Should_NotAllowAction_If_PlayerIsSpectator() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        when(creator.isSpectator()).thenReturn(true);

        GameActionException exception = assertThrows(GameActionException.class, () ->
                mockPokerGame.isIllegalAction(creator, PokerPlayerAction.RAISE));

        assertThat(
                exception.getMessage(),
                containsString("You are not allowed to participate in the game.")
        );
    }

    @Test
    void Should_NotAllowAction_If_ActionIsNotInAllowedList() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        when(creator.canDoAction(PokerPlayerAction.CHECK)).thenReturn(false);
        when(creator.isSpectator()).thenReturn(false);

        GameActionException exception = assertThrows(GameActionException.class, () ->
                mockPokerGame.isIllegalAction(creator, PokerPlayerAction.RAISE));

        assertThat(
                exception.getMessage(),
                containsString("You are not allowed to perform this action.")
        );

    }

    @Test
    void Should_ReturnFalse_If_ActionIsNotIllegal() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        when(creator.canDoAction(PokerPlayerAction.CHECK)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);
        assertFalse(mockPokerGame.isIllegalAction(creator, PokerPlayerAction.CHECK));
    }

    @Test
    void Should_NotBeAbleToCall_If_PlayerHasNotEnoughChips() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        when(creator.canDoAction(PokerPlayerAction.CALL)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);
        when(creator.getChips()).thenReturn(BigInteger.valueOf(100));
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(0));

        GameActionException exception = assertThrows(GameActionException.class, () -> mockPokerGame.call(creator));

        assertThat(
                exception.getMessage(),
                containsString("You do not have enough chips to call.")
        );
    }

    @Test
    void Should_NotBeAbleToCall_If_BetAlreadyMatched() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        when(creator.canDoAction(PokerPlayerAction.CALL)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);
        when(creator.getChips()).thenReturn(BigInteger.valueOf(100));
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(200));


        GameActionException exception = assertThrows(GameActionException.class, () -> mockPokerGame.call(creator));

        assertThat(
                exception.getMessage(),
                containsString("You have already matched the bet.")
        );
    }

    @Test
    void Should_Call() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        when(creator.canDoAction(PokerPlayerAction.CALL)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);
        when(creator.getChips()).thenReturn(BigInteger.valueOf(200));
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(100));

        doNothing().when(creator).call(BigInteger.valueOf(100));
        doNothing().when(mockPokerGame).contributePot(BigInteger.valueOf(100));
        doNothing().when(mockPokerGame).manageBettingRound();

        mockPokerGame.call(creator);

        verify(creator, times(1)).call(BigInteger.valueOf(100));
        verify(mockPokerGame, times(1)).contributePot(BigInteger.valueOf(100));
        verify(mockPokerGame, times(1)).manageBettingRound();
    }

    @Test
    void Should_RotateActor_If_RoundCanNotEnd() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        when(mockPokerGame.canRoundEnd()).thenReturn(false);
        doNothing().when(mockPokerGame).rotateActor(true);

        mockPokerGame.manageBettingRound();

        verify(mockPokerGame, times(1)).rotateActor(true);
    }

    @Test
    void Should_StartNewBettingRound_If_RoundCanEnd() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        when(mockPokerGame.canRoundEnd()).thenReturn(true);
        doNothing().when(mockPokerGame).startNextBettingRound();

        mockPokerGame.manageBettingRound();

        verify(mockPokerGame, times(1)).startNextBettingRound();
    }

    @Test
    void Should_NotAllowCheck_If_PlayerHasNotMatchedBet() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(100));
        when(creator.canDoAction(PokerPlayerAction.CHECK)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);

        GameActionException exception = assertThrows(GameActionException.class, () -> mockPokerGame.check(creator));

        assertThat(
                exception.getMessage(),
                containsString("Your current bet is too low to check.")
        );


    }

    @Test
    void Should_Check() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(200);
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(200));
        when(creator.canDoAction(PokerPlayerAction.CHECK)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);

        doNothing().when(creator).check();
        doNothing().when(mockPokerGame).manageBettingRound();
        mockPokerGame.check(creator);

        verify(creator, times(1)).check();
        verify(mockPokerGame, times(1)).manageBettingRound();

    }

    @Test
    void Should_Fold() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        mockPokerGame.actor = creator;
        mockPokerGame.activePlayers = 2;
        when(creator.canDoAction(PokerPlayerAction.FOLD)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);

        doNothing().when(creator).fold();
        doNothing().when(mockPokerGame).manageBettingRound();
        mockPokerGame.fold(creator);

        verify(creator, times(1)).fold();
        verify(mockPokerGame, times(1)).manageBettingRound();
        assertEquals(1, mockPokerGame.activePlayers);
    }

    @Test
    void Should_NotAllowRaise_If_FixedLimitReached() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        when(mockPokerGame.getTableType()).thenReturn(TableType.FIXED_LIMIT);
        mockPokerGame.raises = 3;
        mockPokerGame.actor = creator;
        when(creator.canDoAction(PokerPlayerAction.RAISE)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);

        GameActionException exception = assertThrows(GameActionException.class, () -> mockPokerGame.raise(creator, BigInteger.valueOf(100)));

        assertThat(
                exception.getMessage(),
                containsString("Maximum number of raises reached for fixed limit.")
        );
    }

    @Test
    void Should_NotAllowRaiseWithLessThanBigBlind() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        when(mockPokerGame.getTableType()).thenReturn(TableType.FIXED_LIMIT);
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});
        mockPokerGame.actor = creator;
        mockPokerGame.currentBlindLevel = 0;
        mockPokerGame.currentBet = BigInteger.valueOf(10);
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(10));
        when(creator.canDoAction(PokerPlayerAction.RAISE)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);

        GameActionException exception = assertThrows(GameActionException.class, () -> mockPokerGame.raise(creator, BigInteger.valueOf(10)));

        assertThat(
                exception.getMessage(),
                containsString("Raise is not high enough.")
        );
    }

    @Test
    void Should_NotAllowRaise_If_PlayerDoesNotHaveEnoughChips() {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        when(mockPokerGame.getTableType()).thenReturn(TableType.FIXED_LIMIT);
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});
        mockPokerGame.actor = creator;
        mockPokerGame.currentBlindLevel = 0;
        mockPokerGame.currentBet = BigInteger.valueOf(10);
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(10));
        when(creator.getChips()).thenReturn(BigInteger.valueOf(50));
        when(creator.canDoAction(PokerPlayerAction.RAISE)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);

        GameActionException exception = assertThrows(GameActionException.class, () -> mockPokerGame.raise(creator, BigInteger.valueOf(100)));

        assertThat(
                exception.getMessage(),
                containsString("You do not have enough chips to raise.")
        );
    }

    @Test
    void Should_Raise() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);
        when(mockPokerGame.getTableType()).thenReturn(TableType.FIXED_LIMIT);
        mockPokerGame.smallBlindLevels = List.of(new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)});
        mockPokerGame.actor = creator;
        mockPokerGame.currentBlindLevel = 0;
        mockPokerGame.currentBet = BigInteger.valueOf(10);
        mockPokerGame.raises = 0;
        mockPokerGame.lastBettor = null;
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(10));
        when(creator.getChips()).thenReturn(BigInteger.valueOf(100));
        when(creator.canDoAction(PokerPlayerAction.RAISE)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);
        doNothing().when(mockPokerGame).rotateActor(true);
        doNothing().when(mockPokerGame).contributePot(BigInteger.valueOf(50));
        doNothing().when(creator).bet(BigInteger.valueOf(50));

        mockPokerGame.raise(creator, BigInteger.valueOf(50));

        verify(mockPokerGame, times(1)).rotateActor(true);
        verify(mockPokerGame, times(1)).contributePot(BigInteger.valueOf(50));
        verify(creator, times(1)).bet(BigInteger.valueOf(50));
        assertEquals(1, mockPokerGame.raises);
        assertEquals(creator, mockPokerGame.lastBettor);
    }

    @Test
    void Should_PerformAllIn() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);

        mockPokerGame.actor = creator;
        mockPokerGame.raises = 0;
        mockPokerGame.currentBet = BigInteger.valueOf(20);
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(100));
        when(creator.getChips()).thenReturn(BigInteger.valueOf(100));
        when(creator.canDoAction(PokerPlayerAction.ALL_IN)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);
        doNothing().when(mockPokerGame).manageBettingRound();

        mockPokerGame.allIn(creator);

        verify(creator, times(1)).bet(BigInteger.valueOf(100));
        verify(mockPokerGame, times(1)).contributePot(BigInteger.valueOf(100));
        assertEquals(1, mockPokerGame.raises);
        assertEquals(creator, mockPokerGame.lastBettor);
        assertEquals(BigInteger.valueOf(100), mockPokerGame.currentBet);
        verify(mockPokerGame, times(1)).manageBettingRound();
        verify(creator, times(1)).setLastAction(PokerPlayerAction.ALL_IN);
    }

    @Test
    void Should_KeepCurrentGameBet_If_AllInPlayerDoesPartialAllIn() throws GameActionException {
        PokerGame mockPokerGame = Mockito.spy(pokerGame);

        mockPokerGame.actor = creator;
        mockPokerGame.currentBet = BigInteger.valueOf(50);
        when(creator.getCurrentBet()).thenReturn(BigInteger.valueOf(20));
        when(creator.getChips()).thenReturn(BigInteger.valueOf(100));
        when(creator.canDoAction(PokerPlayerAction.ALL_IN)).thenReturn(true);
        when(creator.isSpectator()).thenReturn(false);
        doNothing().when(mockPokerGame).manageBettingRound();

        mockPokerGame.allIn(creator);

        assertEquals(BigInteger.valueOf(50), mockPokerGame.currentBet);
    }





}
