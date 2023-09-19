package de.thm.holdem.model.game;

import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Deck;
import de.thm.holdem.model.player.Player;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.service.PokerHandEvaluator;
import de.thm.holdem.settings.PokerGameSettings;
import jdk.jshell.spi.ExecutionControl;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PokerGame extends Game {

    /**
     * Enum which returns all possible states the game can be in
     *
     * <p>
     *      PRE_FLOP: Each player has 2 Cards on his hands and the players can bet
     *      FLOP: First 3 cards on the table will be flipped
     *      TURN: 4th card on the table will be flipped
     *      RIVER: 5th card on the table will be flipped
     *      END: Hands of the players will be evaluated and round ends
     * </p>
     */
    private enum State { PRE_FLOP, FLOP, TURN, RIVER, END }

    /** Stores the set of cards used to play the game */
    private final Deck deck;

    /** Identifier of the game */
    private final String gameId;

    /** Settings of the game */
    private final PokerGameSettings settings;

    /** Time of creation */
    private final LocalDate creationDate;

    /** Alias of the player who created the game */
    private final String creator;

    /** amount of money a player has to pay when joining the game to buy chips for the game. */
    private final int buyIn;

    /** The current blind level */
    int currentBlindLevel;

    /** A list of all small blind levels */
    private List<Integer> smallBlindLevels;

    /** Stores the 3 flop cards (first 3 cards dealt on the table) */
    private List<Card> flopCards;

    /** Stores the river card (4th card dealt on the table) */
    private Card riverCard;

    /** Stores the turn card (5th card dealt on the table) */
    private Card turnCard;

    /** The bet each betting players has to match to stay in the round */
    private int currentBet;

    /** Indicates in which round the game is currently in */
    private State state;

    /** The player with the dealer position */
    private PokerPlayer dealer;

    /** The player with the small-blind position */
    private PokerPlayer smallBlindPlayer;

    /** The player with the big-blind position */
    private PokerPlayer bigBlindPlayer;

    /** The player who is currently making an action */
    private PokerPlayer activePlayer;

    /**
     * Constructor for the poker game
     *
     * @param creator initializes the creator variable with the player which created the game
     * @param buyIn sets the required buy in for the game
     * @param settings sets the settings for the game
     */
    PokerGame(PokerPlayer creator, int buyIn, PokerGameSettings settings) {
        this.creator = creator.getAlias();
        this.buyIn = buyIn;
        this.creationDate = LocalDate.now();
        this.settings = settings;
        playerList = new ArrayList<>(6);
        playerList.add(creator);
        this.gameStatus = GameStatus.WAITING;
        this.gameId = UUID.randomUUID().toString();
        this.deck = new Deck();
        this.currentBlindLevel = 0;
    }

    /**
     * Method to add players to the playerList
     *
     * <p>
     *     Will only add the player if the game is not running or finished and the player is not already in the list.
     *     If the playerList is full, the game will be started.
     * </p>
     *
     * @param player The player that should be added to the #playerList
     */
    public void addPlayer(PokerPlayer player) throws Exception {
        if (this.gameStatus.equals(GameStatus.IN_PROGRESS) || this.gameStatus.equals(GameStatus.FINISHED)) {
            throw new Exception("Game is already running");
        }
        if (!playerList.contains(player) && playerList.size() < settings.getMaxPlayers()) {
            playerList.add(player);
            if(playerList.size() == settings.getMaxPlayers()) {
                startGame();
            }
        }
    }

    /**
     * Method to start the game
     *
     * <p>
     *     The game will only be started if at least 3 players have joined the game and the game is not already running
     *     A new deck will be created, the game will be set to running and dealer, small-blind and big-blind positions will be assigned
     * </p>
     *
     * @return (boolean) returns whether the game has started
     */
    public boolean startGame() {
        if (this.gameStatus.equals(GameStatus.IN_PROGRESS) || this.gameStatus.equals(GameStatus.FINISHED)) return false;
        if(playerList.size() < 3) return false;
        this.gameStatus = GameStatus.IN_PROGRESS;
        dealer = (PokerPlayer) playerList.get(0);
        bigBlindPlayer = (PokerPlayer) playerList.get(2);
        smallBlindPlayer = (PokerPlayer) playerList.get(1);
        smallBlindLevels = BlindHelper.calculateBlindLevels(playerList.size(),
                buyIn, settings.getTotalTournamentTime(), settings.getTimeToRaiseBlinds());
        return true;
    }

    /**
     * Method to deal cards to the players of the game and set blinds
     */
    public void deal() {
        state = State.PRE_FLOP;
        deck.shuffleDeck();

        playerList.forEach(player -> {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            pokerPlayer.reset();
            pokerPlayer.dealCard(deck.drawCard());
            pokerPlayer.dealCard(deck.drawCard());
        });

        int smallBlind = smallBlindLevels.get(currentBlindLevel);
        smallBlindPlayer.bet(smallBlind);
        bigBlindPlayer.bet(smallBlind * 2);
        currentBet = smallBlind * 2;
        getNextTurn(bigBlindPlayer);
    }

    /**
     * Method to set activePlayer to the next player on the table. It will ignore all folded players or
     * players who are all in
     *
     * @param player the player whose turn it currently is
     */
    private void getNextTurn(PokerPlayer player) {
        if(state == State.END) return;

        activePlayer = (PokerPlayer) TurnManager.getNext(playerList, player, true);
        while (activePlayer.isFolded() || activePlayer.isAllIn()) {
            activePlayer = (PokerPlayer) TurnManager.getNext(playerList, activePlayer, true);
        }
    }

    /**
     * Method to move the buttons to the next player if the round has ended
     * @param currentSmallBlind the player who is currently the small blind
     */
    public void setNextBlinds(PokerPlayer currentSmallBlind) {
        smallBlindPlayer = (PokerPlayer) TurnManager.getNext(playerList, currentSmallBlind, true);
        while (smallBlindPlayer.getChips() == 0) {
            smallBlindPlayer = (PokerPlayer) TurnManager.getNext(playerList, smallBlindPlayer, true);
        }

        bigBlindPlayer = (PokerPlayer) TurnManager.getNext(playerList, smallBlindPlayer, true);
        while (bigBlindPlayer.getChips() == 0) {
            bigBlindPlayer = (PokerPlayer) TurnManager.getNext(playerList, bigBlindPlayer, true);
        }
    }

    /**
     * Method to call a bet
     *
     * @param player the player who is calling
     * @return (false) if it's not the players turn, the player has already bet enough, the player doesn't
     * have enough chips to call, (true) if the call was successful
     */
    public boolean call(PokerPlayer player) {
        if(!isPlayerTurn(player) || player.getCurrentBet() == currentBet || (currentBet-player.getCurrentBet() > player.getChips()))
            return false;

        player.bet(currentBet-player.getCurrentBet());
        player.setLastAction(PokerPlayerAction.CALL);

        if(canRoundEnd()) {
            startNextRound();
        } else {
            getNextTurn(player);
        }
        return true;
    }

    /**
     * Method to check a bet
     *
     * @param player the player who is checking
     * @return (false) if it's not the players turn, the players bet doesn't equal the current bet,
     * (true) if the check was successful
     */
    public boolean check(PokerPlayer player) {
        if(!isPlayerTurn(player)) return false;
        if(player.getCurrentBet() < currentBet) return false;

        player.setLastAction(PokerPlayerAction.CHECK);

        if(canRoundEnd()) {
            startNextRound();
        } else {
            getNextTurn(player);
        }
        return true;
    }

    /**
     * Method to raise a bet
     *
     * @param player the player who is raising
     * @param amount the amount the player wants to raise
     * @return (false) if it's not the players turn, the amount he wants to bet is smaller than the current bet,
     * the amount is smaller than the bigBlind or the player does not have enough chips for the raise
     * (true) if the raise was successful
     */
    public boolean raise(PokerPlayer player, int amount) {
        if(!isPlayerTurn(player)) return false;

        int bigBlind = smallBlindLevels.get(currentBlindLevel) * 2;
        if((player.getCurrentBet() + amount) < currentBet || amount < bigBlind || amount > player.getChips()) return false;

        currentBet += amount - (currentBet-player.getCurrentBet());
        player.bet(amount);
        player.setLastAction(PokerPlayerAction.RAISE);
        getNextTurn(player);
        return true;
    }

    /**
     * Method to fold a hand
     *
     * @param player the player that wants to fold
     */
    public void fold(PokerPlayer player) {
        if(!isPlayerTurn(player)) return;

        player.fold();
        player.setLastAction(PokerPlayerAction.FOLD);

        if(canRoundEnd()) {
            startNextRound();
        } else {
            getNextTurn(player);
        }
    }

    /**
     * AllIn has been removed to save some LOC
     *
     * @param player the player that wants to go all in
     */
    public void allIn(Player player) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Not yet implemented");
    }

    /**
     * Method to start a new round
     *
     * <p>
     *     if current state is PRE_FLOP: state will be set to FLOP, 3 Flop cards will be dealt
     *     if current state is FLOP: state will be set to TURN, turn card will be dealt
     *     if current state is TURN: state will be set to RIVER, river card will  be dealt
     *     if current state is RIVER: state will be set to END, hands of all players will be evaluated
     * </p>
     */
    private void startNextRound() {
        switch (state) {
            case PRE_FLOP -> {
                state = State.FLOP;
                flopCards = new ArrayList<>(3);
                deck.burnCard();
                flopCards.add(deck.drawCard());
                flopCards.add(deck.drawCard());
                flopCards.add(deck.drawCard());
                getNextTurn(dealer);
            }
            case FLOP -> {
                deck.burnCard();
                turnCard = deck.drawCard();
                getNextTurn(dealer);
                state = State.TURN;
            }
            case TURN -> {
                deck.burnCard();
                riverCard = deck.drawCard();
                getNextTurn(dealer);
                state = State.RIVER;
            }
            case RIVER -> {
                state = State.END;
                evaluateHands();
            }
        }
    }

    /**
     * Method to prepare the game for a new round
     *
     * <p>
     *      NOT a state change, but the whole playing round ended (the pot was won)
     * </p>
     */
    void prepareNextRound() {
        playerList.removeIf(player -> (player.getChips() == 0 && !((PokerPlayer) player).isFolded()));
        playerList.forEach(Player::reset);
        if(playerList.size() == 1) return;
        dealer = (PokerPlayer) TurnManager.getNext(playerList, dealer, true);
        setNextBlinds(smallBlindPlayer);
        flopCards.clear();
        riverCard = null;
        turnCard = null;
        activePlayer = smallBlindPlayer;
    }

    /**
     * Method to evaluate hands of all players. Will return if the state is not END
     *
     * <p>
     *     for each player that has not folded, the HandEvaluator will be called with the 2 Hand cards,
     *     the 3 flop cards, the river card and the turn card.
     *     The Score of each players hand will be assigned to the player
     * </p>
     */
    public void evaluateHands() {
        if(state != State.END) return;
        playerList.stream()
                .map(player -> (PokerPlayer) player)
                .filter(player -> !player.isFolded())
                .forEach(player -> {
                        PokerHandEvaluator handEvaluator = new PokerHandEvaluator(
                                player.getHand().get(0),
                                player.getHand().get(1),
                                flopCards.get(0),
                                flopCards.get(1),
                                flopCards.get(2),
                                riverCard,
                                turnCard
                        );
                        player.setHandScore(handEvaluator.bestHand());
                });
    }

    /**
     * Method to check if a round can end
     *
     * @return (true) if only 1 player has not folded -> round will be set to end;
     * (true) if the round is PRE_FLOP, it is the big blinds turn, and all players have called the current bet or have folded
     * (true) if the round is NOT PRE_FLOP, it is the dealers turn, and all players have called the current bet or have folded
     */
    private boolean canRoundEnd() {
        if(playerList.stream().map(player -> (PokerPlayer) player).filter(player -> !player.isFolded()).count() == 1) {
            playerList.stream()
                    .map(player -> (PokerPlayer) player)
                    .filter(player -> !player.isFolded()).findFirst().get().setHandScore(1000);
            state = State.END;
            return true;
        } else if(state == State.PRE_FLOP && activePlayer == bigBlindPlayer) {
            long num = playerList.stream()
                    .map(player -> (PokerPlayer) player)
                    .filter(player -> player.getCurrentBet() == currentBet && !player.isFolded()).count();
            return num == playerList.stream().map(player -> (PokerPlayer) player).filter(player -> !player.isFolded()).count();
        } else if(state != State.PRE_FLOP && activePlayer == dealer) {
            long num = playerList.stream()
                    .map(player -> (PokerPlayer) player)
                    .filter(player -> player.getCurrentBet() == currentBet && !player.isFolded()).count();
            return num == playerList.stream().map(player -> (PokerPlayer) player).filter(player -> !player.isFolded()).count();
        }
        return false;
    }

    int getPotSize() { return  playerList.stream().mapToInt(Player::getCurrentBet).sum(); }

    private boolean isPlayerTurn(PokerPlayer player) {
        return player.equals(activePlayer);
    }

}
