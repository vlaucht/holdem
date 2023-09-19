package de.thm.holdem.model.player;

import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Rank;
import de.thm.holdem.model.card.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PokerPlayerTest {

    private PokerPlayer pokerPlayer;
    private final int buyIn = 100;
    private final int bankroll = 1000;

    @BeforeEach
    void setUp() {
        pokerPlayer = new PokerPlayer("Alice");
    }

    @Test
    void Should_DeductBuyInFromPlayersBankroll() {
        pokerPlayer.joinGame(buyIn);
        assertEquals(bankroll - buyIn, pokerPlayer.getBankroll());
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
        assertEquals(0, pokerPlayer.getChips()); // Chips should be reset to 0
        assertEquals(bankroll, pokerPlayer.getBankroll()); // Bankroll should be restored
    }

    @Test
    void Should_NotReturnActiveChipsToPlayer_If_PlayerLeavesGame() {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.bet(50);
        pokerPlayer.leaveGame();
        assertEquals(0, pokerPlayer.getChips());
        assertEquals(bankroll - 50, pokerPlayer.getBankroll());
    }
    @Test
    public void Should_DealCardsToPlayer() {
        Card card1 = new Card(Rank.ACE, Suit.HEARTS);
        Card card2 = new Card(Rank.KING, Suit.SPADES);

        pokerPlayer.dealCard(card1);
        pokerPlayer.dealCard(card2);

        assertEquals(2, pokerPlayer.getHand().size());
        assertTrue(pokerPlayer.getHand().contains(card1));
        assertTrue(pokerPlayer.getHand().contains(card2));
    }

    @Test
    public void Should_ThrowException_If_PlayerGetsTooManyCards() {
        Card card1 = new Card(Rank.ACE, Suit.HEARTS);
        Card card2 = new Card(Rank.KING, Suit.SPADES);
        Card card3 = new Card(Rank.QUEEN, Suit.DIAMONDS);

        pokerPlayer.dealCard(card1);
        pokerPlayer.dealCard(card2);

        assertThrows(IllegalStateException.class, () -> pokerPlayer.dealCard(card3));
    }

    @Test
    void Should_DeductBetFromAvailableChips() {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.bet(50);

        assertEquals(50, pokerPlayer.getCurrentBet());
        assertEquals(50, pokerPlayer.getChips());
    }

    @Test
    public void Should_ThrowException_If_BetIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> pokerPlayer.bet(-50));
    }

    @Test
    public void Should_ThrowException_If_NotEnoughChipsForBet() {
        assertThrows(IllegalArgumentException.class, () -> pokerPlayer.bet(1001));
    }

    @Test
    public void Should_AddToTotalChips_If_PlayerWins() {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.win(10);

        assertEquals(110, pokerPlayer.getChips());
    }

    @Test
    public void Should_FoldPlayer() {
        pokerPlayer.fold();

        assertTrue(pokerPlayer.isFolded());
    }

    @Test
    public void Should_AddToBankroll() {
        pokerPlayer.addToBankroll(500);

        assertEquals(1500, pokerPlayer.getBankroll());
    }

    @Test
    public void Should_ResetPlayer() {
        pokerPlayer.joinGame(buyIn);
        pokerPlayer.bet(50);
        pokerPlayer.fold();
        pokerPlayer.dealCard(new Card(Rank.ACE, Suit.HEARTS));
        pokerPlayer.reset();

        assertEquals(0, pokerPlayer.getCurrentBet());
        assertFalse(pokerPlayer.isFolded());
        assertEquals(0, pokerPlayer.getHand().size());
        // should keep chips and bankroll
        assertEquals(buyIn - 50, pokerPlayer.getChips());
        assertEquals(bankroll - 100, pokerPlayer.getBankroll());
    }


}