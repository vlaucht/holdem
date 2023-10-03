package de.thm.holdem.model.player;

import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Rank;
import de.thm.holdem.model.card.Suit;
import de.thm.holdem.model.game.poker.PokerPlayerAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class PokerPlayerTest {

    private PokerPlayer pokerPlayer;
    private final BigInteger buyIn = BigInteger.valueOf(1000);
    private final BigInteger bankroll = BigInteger.valueOf(10000);

    @BeforeEach
    void setUp() {
        pokerPlayer = new PokerPlayer("test::id", "test::name", "test::avatar", bankroll);
    }

    @Test
    void Should_DeductBuyInFromPlayersBankroll() {
        pokerPlayer.joinGame(buyIn);
        assertEquals(bankroll.subtract(buyIn), pokerPlayer.getBankroll());
    }

    @Test
    void Should_ReturnFalse_If_PlayerHasNoHoleCards() {
        assertFalse(pokerPlayer.hasHoleCards());
    }

    @Test
    void Should_ReturnTrue_If_PlayerCanDoAction() {
        pokerPlayer.addAllowedAction(PokerPlayerAction.CHECK);
        assertTrue(pokerPlayer.canDoAction(PokerPlayerAction.CHECK));
    }

    @Test
    void Should_ReturnZeroAsHandScore_If_PlayerHasNoHandResult() {
        pokerPlayer.reset();
        pokerPlayer.joinGame(buyIn);
        assertEquals(0, pokerPlayer.getHandScore());
    }

    @Test
    void Should_ReturnHandScore() throws GameActionException {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.dealCard(new Card(Rank.ACE, Suit.HEARTS));
        pokerPlayer.dealCard(new Card(Rank.KING, Suit.SPADES));
        pokerPlayer.getHand().addCards(pokerPlayer.getHoleCards());
        assertTrue(pokerPlayer.getHandScore() > 0);
    }

    @Test
    void Should_ReturnZeroAsHandScore_If_PlayerHasFolded() throws GameActionException {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.dealCard(new Card(Rank.ACE, Suit.HEARTS));
        pokerPlayer.dealCard(new Card(Rank.KING, Suit.SPADES));
        pokerPlayer.getHand().addCards(pokerPlayer.getHoleCards());
        pokerPlayer.fold();
        assertEquals(0, pokerPlayer.getHandScore());
    }

    @Test
    void Should_ReturnZeroAsHandScore_If_PlayerIsSpectator() {
        assertEquals(0, pokerPlayer.getHandScore());
    }

    @Test
    void Should_ReturnFalse_If_PlayerCannotDoAction() {
        assertFalse(pokerPlayer.canDoAction(PokerPlayerAction.CHECK));
    }

    @Test
    void Should_ReturnTrue_If_PlayerHasHoleCards() throws GameActionException {
        pokerPlayer.dealCard(new Card(Rank.ACE, Suit.HEARTS));
        pokerPlayer.dealCard(new Card(Rank.KING, Suit.SPADES));

        assertTrue(pokerPlayer.hasHoleCards());
    }

    @Test
    void Should_InitializeChips_If_PlayerJoinsGame() {
        pokerPlayer.joinGame(buyIn);
        assertEquals(buyIn, pokerPlayer.getChips());
    }

    @Test
    void Should_ReturnChipsToPlayer_If_PlayerLeavesGame() {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.leaveGame();
        assertEquals(BigInteger.ZERO, pokerPlayer.getChips()); // Chips should be reset to 0
        assertEquals(bankroll, pokerPlayer.getBankroll()); // Bankroll should be restored
    }

    @Test
    void Should_NotReturnActiveChipsToPlayer_If_PlayerLeavesGame() throws GameActionException {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.bet(BigInteger.valueOf(50));
        pokerPlayer.leaveGame();
        assertEquals(BigInteger.ZERO, pokerPlayer.getChips());
        assertEquals(bankroll.subtract(BigInteger.valueOf(50)), pokerPlayer.getBankroll());
    }
    @Test
    void Should_DealCardsToPlayer() throws GameActionException {
        Card card1 = new Card(Rank.ACE, Suit.HEARTS);
        Card card2 = new Card(Rank.KING, Suit.SPADES);

        pokerPlayer.dealCard(card1);
        pokerPlayer.dealCard(card2);

        assertEquals(2, pokerPlayer.getHoleCards().size());
        assertTrue(pokerPlayer.getHoleCards().contains(card1));
        assertTrue(pokerPlayer.getHoleCards().contains(card2));
    }

    @Test
    void Should_ThrowException_If_PlayerGetsTooManyCards() throws GameActionException {
        Card card1 = new Card(Rank.ACE, Suit.HEARTS);
        Card card2 = new Card(Rank.KING, Suit.SPADES);
        Card card3 = new Card(Rank.QUEEN, Suit.DIAMONDS);

        pokerPlayer.dealCard(card1);
        pokerPlayer.dealCard(card2);

        assertThrows(GameActionException.class, () -> pokerPlayer.dealCard(card3));
    }

    @Test
    void Should_DeductBetFromAvailableChips() throws GameActionException {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.bet(BigInteger.valueOf(50));

        assertEquals(BigInteger.valueOf(50), pokerPlayer.getCurrentBet());
        assertEquals(buyIn.subtract(BigInteger.valueOf(50)), pokerPlayer.getChips());
    }

    @Test
    void Should_ThrowException_If_BetIsNegative() {
        assertThrows(GameActionException.class, () -> pokerPlayer.bet(BigInteger.valueOf(50).negate()));
    }

    @Test
    void Should_ThrowException_If_NotEnoughChipsForBet() {
        assertThrows(GameActionException.class, () -> pokerPlayer.bet(BigInteger.valueOf(1001)));
    }

    @Test
    void Should_AddToTotalChips_If_PlayerWins() {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.win(BigInteger.valueOf(10));

        assertEquals(buyIn.add(BigInteger.valueOf(10)), pokerPlayer.getChips());
    }

    @Test
    void Should_FoldPlayer() {
        pokerPlayer.fold();
        assertEquals(PokerPlayerAction.FOLD, pokerPlayer.getLastAction());
        assertTrue(pokerPlayer.isFolded());
    }

    @Test
    void Should_CheckPlayer() {
        pokerPlayer.check();
        assertEquals(PokerPlayerAction.CHECK, pokerPlayer.getLastAction());
    }

    @Test
    void Should_SetLastAction() {
        pokerPlayer.setLastAction(PokerPlayerAction.CHECK);
        assertEquals(PokerPlayerAction.CHECK, pokerPlayer.getLastAction());
    }

    @Test
    void Should_ReturnTrue_If_PlayerIsAllIn() throws GameActionException {
        pokerPlayer.joinGame(buyIn);
        Card card1 = new Card(Rank.ACE, Suit.HEARTS);
        Card card2 = new Card(Rank.KING, Suit.SPADES);

        pokerPlayer.dealCard(card1);
        pokerPlayer.dealCard(card2);
        pokerPlayer.bet(pokerPlayer.chips);
        assertTrue(pokerPlayer.isAllIn());
    }

    @Test
    void Should_ReturnFalse_If_PlayerIsNotAllIn() throws GameActionException {
        pokerPlayer.joinGame(buyIn);
        Card card1 = new Card(Rank.ACE, Suit.HEARTS);
        Card card2 = new Card(Rank.KING, Suit.SPADES);
        pokerPlayer.dealCard(card1);
        pokerPlayer.dealCard(card2);

        assertFalse(pokerPlayer.isAllIn());
    }

    @Test
    void Should_PayBigBlind() {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.payBigBlind(BigInteger.valueOf(50));
        assertEquals(PokerPlayerAction.BIG_BLIND, pokerPlayer.getLastAction());
        assertEquals(BigInteger.valueOf(50), pokerPlayer.getCurrentBet());
        assertEquals(buyIn.subtract(BigInteger.valueOf(50)), pokerPlayer.getChips());
    }

    @Test
    void Should_PaySmallBlind() {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.paySmallBlind(BigInteger.valueOf(25));
        assertEquals(PokerPlayerAction.SMALL_BLIND, pokerPlayer.getLastAction());
        assertEquals(BigInteger.valueOf(25), pokerPlayer.getCurrentBet());
        assertEquals(buyIn.subtract(BigInteger.valueOf(25)), pokerPlayer.getChips());
    }

    @Test
    void Should_CallBet() {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.call(BigInteger.valueOf(50));
        assertEquals(PokerPlayerAction.CALL, pokerPlayer.getLastAction());
        assertEquals(BigInteger.valueOf(50), pokerPlayer.getCurrentBet());
        assertEquals(buyIn.subtract(BigInteger.valueOf(50)), pokerPlayer.getChips());
    }

    @Test
    void Should_ClearAllowedActions() {
        pokerPlayer.addAllowedAction(PokerPlayerAction.CHECK);
        pokerPlayer.clearAllowedActions();
        assertEquals(0, pokerPlayer.getAllowedActions().size());
    }

    @Test
    void Should_AddAllowedAction() {
        pokerPlayer.addAllowedAction(PokerPlayerAction.CHECK);
        assertEquals(1, pokerPlayer.getAllowedActions().size());
        assertTrue(pokerPlayer.getAllowedActions().contains(PokerPlayerAction.CHECK));
    }

    @Test
    void Should_AddToBankroll() {
        pokerPlayer.addToBankroll(BigInteger.valueOf(500));

        assertEquals(bankroll.add(BigInteger.valueOf(500)), pokerPlayer.getBankroll());
    }

    @Test
    void Should_ResetPlayer() throws GameActionException {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.bet(BigInteger.valueOf(50));
        pokerPlayer.fold();
        pokerPlayer.dealCard(new Card(Rank.ACE, Suit.HEARTS));
        pokerPlayer.reset();

        assertEquals(BigInteger.ZERO, pokerPlayer.getCurrentBet());
        assertFalse(pokerPlayer.isFolded());
        assertNull(pokerPlayer.getLastAction());
        assertEquals(0, pokerPlayer.getHoleCards().size());
        assertEquals(0, pokerPlayer.getAllowedActions().size());
        assertFalse(pokerPlayer.mustShowCards());
        assertEquals(BigInteger.ZERO, pokerPlayer.getPotShare());
        assertNull(pokerPlayer.getHand().getHandResult());

        // should keep chips and bankroll
        assertEquals(buyIn.subtract(BigInteger.valueOf(50)), pokerPlayer.getChips());
        assertEquals(bankroll.subtract(buyIn), pokerPlayer.getBankroll());
    }


}