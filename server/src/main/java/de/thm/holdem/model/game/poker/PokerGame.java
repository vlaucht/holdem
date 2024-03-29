package de.thm.holdem.model.game.poker;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Deck;
import de.thm.holdem.model.game.Game;
import de.thm.holdem.model.game.GameListener;
import de.thm.holdem.model.game.GameStatus;
import de.thm.holdem.model.player.Player;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.settings.PokerGameSettings;
import de.thm.holdem.utils.TurnManager;
import lombok.Getter;

import java.math.BigInteger;
import java.util.*;

// TODO convert BigInteger to long for memory and performance reasons
@Getter
public class PokerGame extends Game {

    /**
     * In fixed-limit games, the maximum number of raises per betting round.
     */
    private static final int MAX_RAISES = 3;

    /**
     * Number of raises in the current betting round.
     */
    protected int raises;

    /**
     * Stores the set of cards used to play the game
     */
    protected Deck deck;

    /**
     * Settings of the game
     */
    private final PokerGameSettings settings;

    /**
     * The maximum amount of players that can join the game
     */
    protected final int maxPlayerCount;

    /**
     * amount of money a player has to pay when joining the game to buy chips for the game.
     */
    private final BigInteger buyIn;

    /**
     * The type of the table
     */
    private final TableType tableType;

    /**
     * The current blind level
     */
    protected int currentBlindLevel;

    /**
     * A list of all small blind levels
     */
    protected List<BigInteger> smallBlindLevels;

    /**
     * Stores the 3 flop cards (first 3 cards dealt on the table)
     */
    protected List<Card> flopCards;

    /**
     * Stores the turn card (4th card dealt on the table)
     */
    protected Card turnCard;

    /**
     * Stores the river card (5th card dealt on the table)
     */
    protected Card riverCard;

    /**
     * The bet each betting players has to match to stay in the round
     */
    protected BigInteger currentBet;

    /**
     * All pots in the current hand (main pot and any side pots).
     */
    protected final List<Pot> pots;

    /**
     * Indicates in which round the game is currently in
     */
    protected BettingRound bettingRound;

    /**
     * The player with the dealer position
     */
    protected PokerPlayer dealer;

    /**
     * The player with the small-blind position
     */
    protected PokerPlayer smallBlindPlayer;

    /**
     * The player with the big-blind position
     */
    protected PokerPlayer bigBlindPlayer;

    /**
     * The player who is currently making an action
     */
    protected PokerPlayer actor;

    /**
     * The player who made the last raise, used to determine showdown order
     */
    protected PokerPlayer lastBettor;

    /**
     * The number of active players (players that have not folded and still have chips) in the game
     */
    protected int activePlayers;


    private List<PokerPlayer> showdownOrder;


    /**
     * Constructor for the poker game
     *
     * @param creator  initializes the creator variable with the player which created the game
     * @param buyIn    sets the required buy in for the game
     * @param settings sets the settings for the game
     */
    public PokerGame(PokerPlayer creator, BigInteger buyIn, PokerGameSettings settings, TableType tableType,
                     int maxPlayerCount, String name) {
        super(name, creator.getId());
        this.raises = 0;
        this.pots = new ArrayList<>();
        this.tableType = tableType;
        this.maxPlayerCount = maxPlayerCount;
        this.buyIn = buyIn;
        this.bettingRound = BettingRound.NONE;
        this.settings = settings;
        this.playerList = new ArrayList<>(maxPlayerCount);
        this.playerList.add(creator);
        this.deck = new Deck();
    }

    /**
     * Method to get a player from the playerList by his id.
     *
     * @param playerId the id of the player.
     * @return the player with the given id or null if no player with the given id exists.
     */
    public Player getPlayerById(String playerId) {
        return playerList.stream().filter(s -> s.getId().equals(playerId)).findFirst().orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePlayer(Player player) {
        playerList.remove(player);
        if (activePlayers > 0) {
            activePlayers--;
        }
    }

    @Override
    public void notifyPlayers(ClientOperation operation) {
        for (GameListener listener : listeners) {
            listener.onNotifyPlayers(this, operation);
        }
    }

    @Override
    public void notifyPlayer(Player player) {
        // TODO
    }

    @Override
    public void notifyGameState(ClientOperation operation) {
        for (GameListener listener : listeners) {
            listener.onNotifyGameState(this, operation);
        }
    }

    /**
     * Method to add players to the playerList
     *
     * <p>
     * Will only add the player if the game is not running or finished and the player is not already in the list.
     * If the playerList is full, the game will be started.
     * </p>
     *
     * @param player The player that should be added to the #playerList
     */
    @Override
    public void addPlayer(Player player) throws Exception {
        if (this.getGameStatus().equals(GameStatus.IN_PROGRESS) || this.getGameStatus().equals(GameStatus.FINISHED)) {
            throw new GameActionException("Game is already running");
        }
        if (playerList.contains(player) || playerList.size() >= maxPlayerCount) {
            throw new GameActionException("Player can not join this game.");
        }
        playerList.add(player);
        if (playerList.size() == maxPlayerCount) {
            startGame();
        }
    }


    /**
     * Method to start the game
     *
     * <p>
     * The game will only be started if at least 2 players have joined the game and the game is not already running.
     * A new deck will be created, the game will be set to running and a random player is chosen as dealer.
     * Then the blind levels will be calculated and the first round will be started.
     * </p>
     */
    public void startGame() throws Exception {
        if (getGameStatus().equals(GameStatus.IN_PROGRESS) || getGameStatus().equals(GameStatus.FINISHED)) {
            throw new GameActionException("Game is already running.");
        }

        if (playerList.size() < 2) {
            throw new GameActionException("Not enough players to start the game.");
        }

        activePlayers = playerList.size();
        gameStatus = GameStatus.IN_PROGRESS;
        // a random player becomes the first dealer
        dealer = (PokerPlayer) getRandomPlayer();
        actor = dealer;
        currentBet = BigInteger.ZERO;
        calculateSmallBlindLevels();
        deal();
    }

    private void calculateSmallBlindLevels() {
        currentBlindLevel = 0;
        smallBlindLevels = BlindHelper.calculateBlindLevels(playerList.size(),
                buyIn, settings.getTotalTournamentTime(), settings.getTimeToRaiseBlinds());
    }

    /**
     * Method to deal two cards to each player and let the small and big blind pay their blinds.
     * The game will be set to PRE_FLOP.
     */
    void deal() throws Exception {
        if (!getGameStatus().equals(GameStatus.IN_PROGRESS)) {
            throw new GameActionException("Game has not started yet.");
        }
        bettingRound = BettingRound.PRE_FLOP;
        deck.shuffle();

        playerList.forEach(player -> {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            pokerPlayer.reset();
            try {
                if (!pokerPlayer.isSpectator()) {
                    pokerPlayer.dealCard(deck.drawCard());
                    pokerPlayer.dealCard(deck.drawCard());
                    pokerPlayer.getHand().addCards(pokerPlayer.getHoleCards());
                }
            } catch (GameActionException e) {
                throw new RuntimeException(e);
            }
        });

        postSmallBlind();
        postBigBlind();
        notifyPlayers(ClientOperation.DEAL);
        notifyGameState(ClientOperation.DEAL);
    }

    /**
     * Method to pay the small blind.
     */
    void postSmallBlind() throws GameActionException {
        rotateActor(false);
        smallBlindPlayer = actor;
        BigInteger smallBlind = smallBlindLevels.get(currentBlindLevel);
        smallBlindPlayer.paySmallBlind(smallBlind);
        contributePot(smallBlind);
        currentBet = smallBlind;
    }

    /**
     * Method to pay the big blind.
     */
    void postBigBlind() throws GameActionException {
        rotateActor(false);
        bigBlindPlayer = actor;
        BigInteger bigBlind = smallBlindLevels.get(currentBlindLevel).multiply(BigInteger.TWO);
        bigBlindPlayer.payBigBlind(bigBlind);
        contributePot(bigBlind);
        currentBet = bigBlind;
        rotateActor(true);
    }

    void contributePot(BigInteger amount) {
        if (pots.isEmpty()) {
            pots.add(new Pot());
        }
        pots.get(pots.size() - 1).contribute(actor, amount);
    }

    void checkForSplitPots() {
        Pot currentPot = pots.get(pots.size() - 1);
        PokerPlayer allInPlayerWithSmallestStack = currentPot.getAllInPlayerWithSmallestStack();
        while (allInPlayerWithSmallestStack != null) {
            Pot sidePot = currentPot.split(allInPlayerWithSmallestStack);
            pots.add(sidePot);
            currentPot = sidePot;
            allInPlayerWithSmallestStack = currentPot.getAllInPlayerWithSmallestStack();
        }
    }


    /**
     * Method to increase the blind level. This should be called after the
     * blind timer has expired and before a new round starts.
     *
     * @throws GameActionException if the blinds are raised during a round.
     */
    public void raiseBlinds() throws GameActionException {
        if (bettingRound.isAfter(BettingRound.NONE) && bettingRound.isBefore(BettingRound.END)) {
            throw new GameActionException("Blinds can not be raised during a round.");
        }
        if (currentBlindLevel < smallBlindLevels.size() - 1) {
            currentBlindLevel++;
        }
    }


    /**
     * Method to set actor to the next player on the table. It will ignore all folded players or
     * players who are all in or spectators.
     *
     * @param getAllowedActions if true, the allowed actions for the new actor will be set.
     */
    void rotateActor(boolean getAllowedActions) throws GameActionException {
        if (bettingRound == BettingRound.END) return;
        actor.clearAllowedActions();
        actor = (PokerPlayer) TurnManager.getNext(playerList, actor, true);
        while (actor.isFolded() || actor.isAllIn() || actor.isSpectator()) {
            actor = (PokerPlayer) TurnManager.getNext(playerList, actor, true);
        }

        if (getAllowedActions) {
            setAllowedActions();
        }
    }

    /**
     * Method to set the allowed actions for the current actor.
     *
     * <p>
     * Allowed actions are determined by the current game state, so that the actor
     * can not perform any illegal actions. If the actor is all in, he will automatically check.
     * </p>
     */
    void setAllowedActions() throws GameActionException {
        actor.clearAllowedActions();
        // actor will automatically check if he is all in
        if (actor.getLastAction() != null && actor.getLastAction().equals(PokerPlayerAction.ALL_IN)) {
            check(actor);
            notifyPlayers(ClientOperation.PLAYER_ACTION);
            return;
        }

        BigInteger currentChips = actor.getChips();
        BigInteger chipsNeededToCall = currentBet.subtract(actor.getCurrentBet());
        BigInteger bigBlind = smallBlindLevels.get(currentBlindLevel).multiply(BigInteger.TWO);

        // player can always fold
        actor.addAllowedAction(PokerPlayerAction.FOLD);

        // player can only do all-in when he has fewer chips than the current bet
        if (currentChips.compareTo(chipsNeededToCall) < 0) {
            actor.addAllowedAction(PokerPlayerAction.ALL_IN);
            return;
        }

        // player can check if he has already matched the current bet
        if (chipsNeededToCall.equals(BigInteger.ZERO)) {
            actor.addAllowedAction(PokerPlayerAction.CHECK);
            //player can only all-in if he has fewer chips than big blind left
            if (currentChips.compareTo(bigBlind) < 0 && currentChips.compareTo(BigInteger.ZERO) > 0) {
                actor.addAllowedAction(PokerPlayerAction.ALL_IN);
                return;
            }
        }
        // player can call, if his bet is smaller than the current bet
        if (chipsNeededToCall.compareTo(BigInteger.ZERO) > 0) {
            actor.addAllowedAction(PokerPlayerAction.CALL);
        }
        // if player has more chips than he needs to call plus the big blind, he can raise
        if (currentChips.compareTo(chipsNeededToCall.add(bigBlind)) > 0 && (getTableType().equals(TableType.NO_LIMIT) || getRaises() < MAX_RAISES)) {
            actor.addAllowedAction(PokerPlayerAction.RAISE);
        }
        // player can always all-in if he has chips left
        if (currentChips.compareTo(BigInteger.ZERO) > 0) {
            actor.addAllowedAction(PokerPlayerAction.ALL_IN);
        }

    }

    /**
     * Method to move the dealer to the next player if the round has ended
     */
    void setNextDealer() {
        dealer = (PokerPlayer) TurnManager.getNext(playerList, dealer, true);
        while (dealer.isSpectator()) {
            dealer = (PokerPlayer) TurnManager.getNext(playerList, dealer, true);
        }
        actor = dealer;
    }

    /**
     * Method to check whether it is illegal for a player to perform an action.
     *
     * <p>
     * The player is not allowed to perform an action if it is not his turn, if he is a spectator,
     * or if the action he is trying to perform is not in his list of allowed actions.
     * </p>
     *
     * @param player the player who is trying to perform an action
     * @param action the action the player is trying to perform
     * @return true if the player is not allowed to perform the action, false otherwise.
     * @throws GameActionException if the player is not allowed to perform the action.
     */
    boolean isIllegalAction(PokerPlayer player, PokerPlayerAction action) throws GameActionException {
        if (!isPlayerTurn(player)) {
            throw new GameActionException("It is not your turn.");
        }
        if (player.isSpectator()) {
            throw new GameActionException("You are not allowed to participate in the game.");
        }
        if (!player.canDoAction(action)) {
            throw new GameActionException("You are not allowed to perform this action.");
        }
        return false;
    }
    

    /**
     * Method to perform a call action.
     *
     * <p>
     * The player will only be allowed to call if he has enough chips to match the current bet.
     * </p>
     *
     * @param player the player who is calling
     */
    public void call(PokerPlayer player) throws GameActionException {
        if (isIllegalAction(player, PokerPlayerAction.CALL)) {
            return;
        }

        BigInteger bet = currentBet.subtract(player.getCurrentBet());
        player.call(bet);
        contributePot(bet);

        manageBettingRound();
    }

    /**
     * Method to manage the end of a round.
     *
     * <p>
     * If the round can end, the next round will be started,
     * otherwise the next player will be set as active player.
     * </p>
     */
    void manageBettingRound() throws GameActionException {
        actor.clearAllowedActions();
        // if only one player is left, the round ends and the remaining cards stay hidden
        if (activePlayers == 1) {
            bettingRound = BettingRound.END;
            checkForSplitPots();
            distributePot();
            // TODO add the winner to the showown order list but he doesnt have to show cards
            notifyPlayers(ClientOperation.PLAYER_WINS);
            notifyGameState(ClientOperation.PLAYER_WINS);
            return;
        }

        // if all players are all-in, the round ends and the remaining cards are dealt
        if (countPlayersThatAreAllIn() == activePlayers) {
            if (flopCards == null || flopCards.isEmpty()) {
                dealCommunityCards("flop");
            }
            if (turnCard == null) {
                dealCommunityCards("turn");
            }
            if (riverCard == null) {
                dealCommunityCards("river");
            }
            finishRound();
            return;
        }

        if (canStartNextBettingRound()) {
            startNextBettingRound();
        } else {
            // continue betting in this round
            rotateActor(true);
            notifyGameState(ClientOperation.PLAYER_ACTION);
            notifyPlayers(ClientOperation.PLAYER_ACTION);
        }
    }

    /**
     * Method to perform a check action.
     *
     * <p>
     * The player will only be allowed to check if he has already matched the current bet.
     * </p>
     *
     * @param player the player who is checking
     */
    public void check(PokerPlayer player) throws GameActionException {
        if (isIllegalAction(player, PokerPlayerAction.CHECK)) {
            return;
        }

        player.check();

        manageBettingRound();
    }

    /**
     * Method to perform a fold action.
     *
     * <p>
     * If the player folds, the number of active players will be decreased by 1 as he is no longer
     * participating in the round.
     * </p>
     *
     * @param player the player that wants to fold
     */
    public void fold(PokerPlayer player) throws GameActionException {
        if (isIllegalAction(player, PokerPlayerAction.FOLD)) {
            return;
        }

        player.fold();
        this.activePlayers--;
        manageBettingRound();
    }

    /**
     * Method to perform a raise action.
     *
     * <p>
     * The player will only be allowed to raise if he has enough chips to match the current bet plus the big blind.
     * </p>
     *
     * @param player the player who is raising
     * @param raise  the amount the player wants to raise
     */
    public void raise(PokerPlayer player, BigInteger raise) throws GameActionException {
        if (isIllegalAction(player, PokerPlayerAction.RAISE)) {
            return;
        }

        raises++;
        lastBettor = player;
        player.bet(raise);
        contributePot(raise);
        currentBet = player.getCurrentBet();
        player.setLastAction(PokerPlayerAction.RAISE);

        manageBettingRound();
    }

    /**
     * Method to perform an all-in action.
     *
     * @param player the player who is going all-in.
     */
    public void allIn(PokerPlayer player) throws GameActionException {
        if (isIllegalAction(player, PokerPlayerAction.ALL_IN)) {
            return;
        }
        BigInteger allIn = player.getChips();
        player.bet(allIn);
        contributePot(allIn);
        raises++;
        lastBettor = player;
        currentBet = currentBet.max(player.getCurrentBet());
        player.setLastAction(PokerPlayerAction.ALL_IN);

        manageBettingRound();
    }

    /**
     * Method to determine whether it is the players turn.
     *
     * @param player the player to check
     * @return true if it is the players turn, false otherwise.
     */
    boolean isPlayerTurn(PokerPlayer player) {
        return player.equals(actor);
    }

    /**
     * Method to check whether a round can end
     *
     * @return (true) if the round is PRE_FLOP, it is the big blinds turn, and all players have called the current bet or have folded,
     * this is, because the big blind was the big blind is the last player to act in the pre-flop round;
     * (true) if the round is NOT PRE_FLOP, it is the dealers turn, and all players have called the current bet or have folded,
     * this is, because the dealer is the last player to act in the flop, turn and river round;
     */
    boolean canStartNextBettingRound() {
        if (bettingRound == BettingRound.PRE_FLOP && actor == bigBlindPlayer) {
            return countPlayersThatCalledAndNotFolded() == activePlayers;
        } else if (bettingRound != BettingRound.PRE_FLOP && actor == dealer) {
            return countPlayersThatCalledAndNotFolded() == activePlayers;
        }
        return false;
    }

    /**
     * Method do count all players that have called the current bet and have not folded.
     *
     * @return the number of players that have called the current bet and have not folded.
     */
    private int countPlayersThatCalledAndNotFolded() {
        return (int) playerList.stream()
                .map(player -> (PokerPlayer) player)
                .filter(player -> (player.getCurrentBet().compareTo(currentBet) == 0 || player.isAllIn()) && !player.isFolded() && !player.isSpectator()).count();
    }

    private int countPlayersThatAreAllIn() {
        return (int) playerList.stream()
                .map(player -> (PokerPlayer) player)
                .filter(player -> player.isAllIn() && !player.isSpectator() && !player.isFolded()).count();
    }

    /**
     * Method to prepare the game for a new round
     *
     * <p>
     * NOT a betting round change, but the whole playing round ended (the pot was won)
     * </p>
     */
    void finishHand() throws Exception {
        playerList.forEach(Player::reset);
        activePlayers = (int) playerList.stream()
                .filter(player -> player.getChips().compareTo(BigInteger.ZERO) > 0).count();

        if (activePlayers < 2) {
            endGame();
            return;
        }
        cleanupHand();
    }

    private void cleanupHand() throws Exception {
        bettingRound = BettingRound.NONE;
        raises = 0;
        flopCards.clear();
        riverCard = null;
        lastBettor = null;
        turnCard = null;
        currentBet = BigInteger.ZERO;
        pots.clear();
        setNextDealer();

        deal();
    }

    private void dealCommunityCards(String type) {
        switch (type) {
            case "flop" -> {
                flopCards = new ArrayList<>(3);
                deck.burnCard();
                flopCards.add(deck.drawCard());
                flopCards.add(deck.drawCard());
                flopCards.add(deck.drawCard());
                addCardsToPlayerHands(flopCards);
            }
            case "turn" -> {
                deck.burnCard();
                turnCard = deck.drawCard();
                addCardsToPlayerHands(List.of(turnCard));
            }
            case "river" -> {
                deck.burnCard();
                riverCard = deck.drawCard();
                addCardsToPlayerHands(List.of(riverCard));
            }
        }
    }

    public void endGame() {
        gameStatus = GameStatus.FINISHED;
    }

    void finishRound() throws GameActionException {
        bettingRound = BettingRound.END;
        checkForSplitPots();
        doShowdown();
        notifyGameState(ClientOperation.SHOWDOWN);
        notifyPlayers(ClientOperation.SHOWDOWN);
    }

    /**
     * Method to start a new betting round.
     *
     * <p>
     * if current state is PRE_FLOP: state will be set to FLOP, 3 Flop cards will be dealt
     * if current state is FLOP: state will be set to TURN, turn card will be dealt
     * if current state is TURN: state will be set to RIVER, river card will  be dealt
     * if current state is RIVER: state will be set to END, hands of all players will be evaluated
     * </p>
     */
    void startNextBettingRound() throws GameActionException {
        switch (bettingRound) {
            case PRE_FLOP -> {
                bettingRound = BettingRound.FLOP;
                dealCommunityCards("flop");
                changeBettingRound();
            }
            case FLOP -> {
                bettingRound = BettingRound.TURN;
                dealCommunityCards("turn");
                changeBettingRound();
            }
            case TURN -> {
                bettingRound = BettingRound.RIVER;
                dealCommunityCards("river");
                changeBettingRound();
            }
            case RIVER -> {
               finishRound();
            }
        }
    }


    private void changeBettingRound() throws GameActionException {
        actor = dealer;
        checkForSplitPots();
        rotateActor(true);
        notifyGameState(ClientOperation.ROUND_CHANGE);
        notifyPlayers(ClientOperation.ROUND_CHANGE);
    }


    private void addCardsToPlayerHands(List<Card> cards) {
        for (Player player : playerList) {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            if (pokerPlayer.isFolded() || pokerPlayer.isSpectator()) {
                continue;
            }
            pokerPlayer.getHand().addCards(cards);
        }
    }

    private BigInteger getTotalPot() {
        BigInteger totalPot = BigInteger.ZERO;
        for (Pot pot : pots) {
            totalPot = totalPot.add(pot.getPotSize());
        }
        return totalPot;
    }


    private List<PokerPlayer> determineShowdownOrder() throws GameActionException {
        // Determine show order; start with all-in players...
        List<PokerPlayer> showingPlayers = new ArrayList<>();
        for (Player player : playerList) {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            if (pokerPlayer.isAllIn()) {
                showingPlayers.add(pokerPlayer);
            }
        }
        // ...then last player to bet or raise (aggressor)...
        if (lastBettor != null) {
            if (!showingPlayers.contains(lastBettor)) {
                showingPlayers.add(lastBettor);
            }
        }
        //...and finally the remaining players, that have not folded starting left of the dealer.
        actor = dealer;
        while (showingPlayers.size() < activePlayers) {
            rotateActor(false);
            if (!showingPlayers.contains(actor)) {
                showingPlayers.add(actor);
            }
        }

        return showingPlayers;
    }


    private void distributePot() {
        if (bettingRound != BettingRound.END) return;
        Map<PokerPlayer, BigInteger> potDivision = new HashMap<>();
        for (Pot pot : pots) {
            Set<PokerPlayer> winners = pot.getWinners();
            // Calculate the pot division and remainder
            BigInteger[] divisionResult = pot.getPotSize().divideAndRemainder(new BigInteger(String.valueOf(winners.size())));
            BigInteger potShare = divisionResult[0]; // Pot share for each winner
            BigInteger remainder = divisionResult[1]; // Remaining odd chips
            for (PokerPlayer winner : winners) {
                potDivision.merge(winner, potShare, BigInteger::add);
            }

            // Distribute any odd chips one by one to the winners in order
            int chipsToDistribute = 1;
            for (PokerPlayer winner : winners) {
                if (chipsToDistribute > remainder.intValue()) {
                    break; // No more odd chips to distribute
                }
                potDivision.merge(winner, BigInteger.ONE, BigInteger::add);
                chipsToDistribute++;
            }
        }

        // distribute winnings to players
        for (PokerPlayer winner : potDivision.keySet()) {
            BigInteger potShare = potDivision.get(winner);
            winner.win(potShare);
            winner.setPotShare(potShare);
        }
    }


    /**
     * Performs the showdown.
     */
    private void doShowdown() throws GameActionException {
        if (bettingRound != BettingRound.END) return;
        showdownOrder = determineShowdownOrder();
        int bestHandValue = -1;
        for (PokerPlayer playerToShow : showdownOrder) {
            if (playerToShow.isAllIn()) {
                playerToShow.mustShowCards(true);
            }
            // players other than all-in players only have to show when having a chance to win
            if (bestHandValue <= playerToShow.getHandScore() && !playerToShow.isFolded()) {
                bestHandValue = playerToShow.getHandScore();
                playerToShow.mustShowCards(true);
            }
        }

        distributePot();

    }

}
