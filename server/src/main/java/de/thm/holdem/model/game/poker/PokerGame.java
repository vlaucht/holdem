package de.thm.holdem.model.game.poker;

import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.exception.IllegalGameActionException;
import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Deck;
import de.thm.holdem.model.game.Game;
import de.thm.holdem.model.game.GameStatus;
import de.thm.holdem.utils.TurnManager;
import de.thm.holdem.model.player.Player;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.utils.PokerHandEvaluator;
import de.thm.holdem.settings.PokerGameSettings;
import de.thm.holdem.utils.ClassFactory;
import jdk.jshell.spi.ExecutionControl;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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
    enum State { PRE_FLOP, FLOP, TURN, RIVER, END }

    /** Stores the set of cards used to play the game */
    protected Deck deck;

    /** Settings of the game */
    private final PokerGameSettings settings;

    /** The maximum amount of players that can join the game */
    private final int maxPlayerCount;

    /** Time of creation */
    private final LocalDate creationDate;

    /** Alias of the player who created the game */
    private final String creator;

    /** amount of money a player has to pay when joining the game to buy chips for the game. */
    private final BigInteger buyIn;

    /** The type of the table */
    private final TableType tableType;

    /** The current blind level */
    private int currentBlindLevel;

    /** A list of all small blind levels */
    protected List<Integer> smallBlindLevels;

    /** Stores the 3 flop cards (first 3 cards dealt on the table) */
    protected List<Card> flopCards;

    /** Stores the turn card (4th card dealt on the table) */
    protected Card turnCard;

    /** Stores the river card (5th card dealt on the table) */
    protected Card riverCard;

    /** The bet each betting players has to match to stay in the round */
    protected int currentBet;

    /** Indicates in which round the game is currently in */
    protected State state;

    /** The player with the dealer position */
    protected PokerPlayer dealer;

    /** The player with the small-blind position */
    protected PokerPlayer smallBlindPlayer;

    /** The player with the big-blind position */
    protected PokerPlayer bigBlindPlayer;

    /** The player who is currently making an action */
    protected PokerPlayer activePlayer;

    protected ClassFactory<PokerHandEvaluator> evaluatorFactory;


    /**
     * Constructor for the poker game
     *
     * @param creator initializes the creator variable with the player which created the game
     * @param buyIn sets the required buy in for the game
     * @param settings sets the settings for the game
     */
    public PokerGame(PokerPlayer creator, BigInteger buyIn, PokerGameSettings settings, TableType tableType, int maxPlayerCount) {
        this.tableType = tableType;
        this.maxPlayerCount = maxPlayerCount;
        creator.joinGame(buyIn);
        this.creator = creator.getAlias();
        this.buyIn = buyIn;
        this.creationDate = LocalDate.now();
        this.settings = settings;
        playerList = new ArrayList<>(6);
        playerList.add(creator);
        this.gameStatus = GameStatus.WAITING;
        this.deck = new Deck();
        this.currentBlindLevel = 0;
        this.evaluatorFactory = new ClassFactory<>(PokerHandEvaluator.class);
    }

   /* *//**
     * Method to add players to the playerList
     *
     * <p>
     *     Will only add the player if the game is not running or finished and the player is not already in the list.
     *     If the playerList is full, the game will be started.
     * </p>
     *
     * @param player The player that should be added to the #playerList
     *//*
    public void addPlayer(PokerPlayer player) throws Exception {
        if (this.gameStatus.equals(GameStatus.IN_PROGRESS) || this.gameStatus.equals(GameStatus.FINISHED)) {
            throw new IllegalGameActionException("Game is already running");
        }
        if (!playerList.contains(player) && playerList.size() < settings.getMaxPlayers()) {
            player.joinGame(buyIn);
            playerList.add(player);
            if(playerList.size() == settings.getMaxPlayers()) {
                startGame();
            }
        }
    }

    *//**
     * Method to start the game
     *
     * <p>
     *     The game will only be started if at least 3 players have joined the game and the game is not already running
     *     A new deck will be created, the game will be set to running and dealer, small-blind and big-blind positions will be assigned
     * </p>
     *//*
    public void startGame() throws Exception {
        if (gameStatus.equals(GameStatus.IN_PROGRESS) || gameStatus.equals(GameStatus.FINISHED)) {
            throw new IllegalGameActionException("Game is already running");
        }

        if(playerList.size() < 3) {
            throw new GameActionException("Not enough players to start the game");
        }

        gameStatus = GameStatus.IN_PROGRESS;
        dealer = (PokerPlayer) playerList.get(0);
        bigBlindPlayer = (PokerPlayer) playerList.get(2);
        smallBlindPlayer = (PokerPlayer) playerList.get(1);
        smallBlindLevels = BlindHelper.calculateBlindLevels(playerList.size(),
                buyIn, settings.getTotalTournamentTime(), settings.getTimeToRaiseBlinds());
    }

    *//**
     * Method to increase the blind level
     *//*
    public void raiseBlinds() {
        currentBlindLevel++;
    }

    *//**
     * Method to deal cards to the players of the game and set blinds
     *//*
    public void deal() throws Exception {
        if (!gameStatus.equals(GameStatus.IN_PROGRESS)) {
            throw new IllegalGameActionException("Game has not started yet.");
        }
        state = State.PRE_FLOP;
        deck.shuffleDeck();

        playerList.forEach(player -> {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            pokerPlayer.reset();
            try {
                pokerPlayer.dealCard(deck.drawCard());
                pokerPlayer.dealCard(deck.drawCard());
            } catch (GameActionException e) {
                throw new RuntimeException(e);
            }
        });

        int smallBlind = smallBlindLevels.get(currentBlindLevel);
        smallBlindPlayer.bet(smallBlind);
        bigBlindPlayer.bet(smallBlind * 2);
        currentBet = smallBlind * 2;
        setNextActivePlayer(bigBlindPlayer);
    }

    *//**
     * Method to set activePlayer to the next player on the table. It will ignore all folded players or
     * players who are all in
     *
     * @param player the player whose turn it currently is
     *//*
    void setNextActivePlayer(PokerPlayer player) {
        if(state == State.END) return;

        activePlayer = (PokerPlayer) TurnManager.getNext(playerList, player, true);
        while (activePlayer.isFolded() || activePlayer.isAllIn()) {
            activePlayer = (PokerPlayer) TurnManager.getNext(playerList, activePlayer, true);
        }
    }

    *//**
     * Method to move the buttons to the next player if the round has ended
     * @param currentSmallBlind the player who is currently the small blind
     *//*
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

    *//**
     * Method to move the dealer to the next player if the round has ended
     *
     * @param currentDealer the player who is currently the dealer
     *//*
    public void setNextDealer(PokerPlayer currentDealer) {
        dealer = (PokerPlayer) TurnManager.getNext(playerList, currentDealer, true);
        while (dealer.getChips() == 0) {
            dealer = (PokerPlayer) TurnManager.getNext(playerList, dealer, true);
        }
    }


    *//**
     * Method to call a bet
     *
     * @param player the player who is calling
     *//*
    public void call(PokerPlayer player) throws Exception {
        if(!isPlayerTurn(player)) {
            throw new IllegalGameActionException("It is not the players turn.");
        }

        if (player.getCurrentBet() == currentBet) {
            throw new GameActionException("Player has already matched the bet.");
        }

        if((currentBet-player.getCurrentBet() > player.getChips())) {
            throw new GameActionException("Player does not have enough chips to call.");
        }

        player.bet(currentBet-player.getCurrentBet());
        player.setLastAction(PokerPlayerAction.CALL);

        manageRoundEnd();
    }

    *//**
     * Method to manage the end of a round
     *
     * <p>
     *     if the round can end, the next round will be started,
     *     else the next player will be set as active player
     * </p>
     *//*
    void manageRoundEnd() {
        if(canRoundEnd()) {
            startNextRound();
        } else {
            setNextActivePlayer(activePlayer);
        }
    }

    *//**
     * Method to check a bet
     *
     * @param player the player who is checking
     *//*
    public void check(PokerPlayer player) throws Exception {
        if(!isPlayerTurn(player)) {
            throw new IllegalGameActionException("It is not the players turn.");
        }
        if(player.getCurrentBet() < currentBet) {
            throw new GameActionException("Player has not bet enough to check.");
        }

        player.setLastAction(PokerPlayerAction.CHECK);

        manageRoundEnd();
    }

    *//**
     * Method to raise a bet
     *
     * @param player the player who is raising
     * @param amount the amount the player wants to raise
     *//*
    public void raise(PokerPlayer player, int amount) throws Exception {
        if(!isPlayerTurn(player)) {
            throw new IllegalGameActionException("It is not the players turn.");
        }
        int smallBlind = smallBlindLevels.get(currentBlindLevel);
        if ((player.getCurrentBet() + amount) < (currentBet + smallBlind)) {
            throw new GameActionException("Raise is not high enough.");
        }

        if(amount > player.getChips()) {
            throw new GameActionException("Player does not have enough chips to raise.");
        }

        player.bet(amount + (currentBet - player.getCurrentBet()));
        currentBet += amount;
        player.setLastAction(PokerPlayerAction.RAISE);
        setNextActivePlayer(player);
    }

    *//**
     * Method to fold a hand
     *
     * @param player the player that wants to fold
     *//*
    public void fold(PokerPlayer player) throws IllegalGameActionException {
        if(!isPlayerTurn(player)) {
            throw new IllegalGameActionException("It is not the players turn.");
        }

        player.fold();
        player.setLastAction(PokerPlayerAction.FOLD);

        manageRoundEnd();
    }

    *//**
     * AllIn has been removed to save some LOC
     *
     * @param player the player that wants to go all in
     *//*
    public void allIn(Player player) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Not yet implemented");
    }

    *//**
     * Method to start a new round
     *
     * <p>
     *     if current state is PRE_FLOP: state will be set to FLOP, 3 Flop cards will be dealt
     *     if current state is FLOP: state will be set to TURN, turn card will be dealt
     *     if current state is TURN: state will be set to RIVER, river card will  be dealt
     *     if current state is RIVER: state will be set to END, hands of all players will be evaluated
     * </p>
     *//*
    void startNextRound() {
        switch (state) {
            case PRE_FLOP -> {
                state = State.FLOP;
                flopCards = new ArrayList<>(3);
                deck.burnCard();
                flopCards.add(deck.drawCard());
                flopCards.add(deck.drawCard());
                flopCards.add(deck.drawCard());
                setNextActivePlayer(dealer);
            }
            case FLOP -> {
                deck.burnCard();
                turnCard = deck.drawCard();
                setNextActivePlayer(dealer);
                state = State.TURN;
            }
            case TURN -> {
                deck.burnCard();
                riverCard = deck.drawCard();
                setNextActivePlayer(dealer);
                state = State.RIVER;
            }
            case RIVER -> {
                state = State.END;
                evaluateHands();
            }
        }
    }

    *//**
     * Method to prepare the game for a new round
     *
     * <p>
     *      NOT a state change, but the whole playing round ended (the pot was won)
     * </p>
     *//*
    void prepareNextRound() {
        if (playerList.stream().filter(player -> player.getChips() > 0).count() < 2) {
            gameStatus = GameStatus.FINISHED;
            return;
        }
        setNextBlinds(smallBlindPlayer);
        setNextDealer(dealer);
        playerList.removeIf(player -> (player.getChips() == 0));
        playerList.forEach(Player::reset);
        flopCards.clear();
        riverCard = null;
        turnCard = null;
        activePlayer = smallBlindPlayer;
    }

    *//**
     * Method to evaluate hands of all players. Will return if the state is not END
     *
     * <p>
     *     for each player that has not folded, the HandEvaluator will be called with the 2 Hand cards,
     *     the 3 flop cards, the river card and the turn card.
     *     The Score of each players hand will be assigned to the player
     * </p>
     *//*
    public void evaluateHands() {
        if(state != State.END) return;

        playerList.stream()
                .map(player -> (PokerPlayer) player)
                .filter(player -> !player.isFolded())
                .forEach(player -> {
                    PokerHandEvaluator handEvaluator;
                    try {
                        handEvaluator = evaluatorFactory.createInstance(
                                    player.getHand().get(0),
                                    player.getHand().get(1),
                                    flopCards.get(0),
                                    flopCards.get(1),
                                    flopCards.get(2),
                                    riverCard,
                                    turnCard);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                    player.setHandScore(handEvaluator.bestHand());
                });
    }

    *//**
     * Method to check whether a round can end
     *
     * @return (true) if only 1 player has not folded -> round will be set to end;
     * (true) if the round is PRE_FLOP, it is the big blinds turn, and all players have called the current bet or have folded
     * (true) if the round is NOT PRE_FLOP, it is the dealers turn, and all players have called the current bet or have folded
     *//*
    boolean canRoundEnd() {
        if (playerList.stream().map(player -> (PokerPlayer) player).filter(player -> !player.isFolded()).count() == 1) {
            playerList.stream()
                    .map(player -> (PokerPlayer) player)
                    .filter(player -> !player.isFolded()).findFirst().get().setHandScore(1000);
            state = State.END;
            return true;
        } else if (state == State.PRE_FLOP && activePlayer == bigBlindPlayer) {
            return countPlayersThatCalledAndNotFolded() == countPlayersThatNotFolded();
        } else if (state != State.PRE_FLOP && activePlayer == dealer) {
            return countPlayersThatCalledAndNotFolded() == countPlayersThatNotFolded();
        }
        return false;
    }

    private int countPlayersThatCalledAndNotFolded() {
        return (int) playerList.stream()
                .map(player -> (PokerPlayer) player)
                .filter(player -> player.getCurrentBet() == currentBet && !player.isFolded()).count();
    }

    private int countPlayersThatNotFolded() {
        return (int) playerList.stream()
                .map(player -> (PokerPlayer) player)
                .filter(player -> !player.isFolded()).count();
    }

    int getPotSize() { return  playerList.stream().mapToInt(Player::getCurrentBet).sum(); }

    private boolean isPlayerTurn(PokerPlayer player) {
        return player.equals(activePlayer);
    }
*/
}
