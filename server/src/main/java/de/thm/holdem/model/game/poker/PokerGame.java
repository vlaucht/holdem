package de.thm.holdem.model.game.poker;

import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.model.card.Card;
import de.thm.holdem.model.card.Deck;
import de.thm.holdem.model.game.Game;
import de.thm.holdem.model.game.GameStatus;
import de.thm.holdem.model.player.Player;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.utils.PokerHandEvaluator;
import de.thm.holdem.settings.PokerGameSettings;
import de.thm.holdem.utils.ClassFactory;
import de.thm.holdem.utils.TurnManager;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;


@Getter
public class PokerGame extends Game {

    /** In fixed-limit games, the maximum number of raises per betting round. */
    private static final int MAX_RAISES = 3;

    /** Number of raises in the current betting round. */
    private int raises;

    /** Stores the set of cards used to play the game */
    protected Deck deck;

    /** Settings of the game */
    private final PokerGameSettings settings;

    /** The maximum amount of players that can join the game */
    private final int maxPlayerCount;

    /** amount of money a player has to pay when joining the game to buy chips for the game. */
    private final BigInteger buyIn;

    /** The type of the table */
    private final TableType tableType;

    /** The current blind level */
    private int currentBlindLevel;

    /** A list of all small blind levels */
    protected List<BigInteger> smallBlindLevels;

    /** Stores the 3 flop cards (first 3 cards dealt on the table) */
    protected List<Card> flopCards;

    /** Stores the turn card (4th card dealt on the table) */
    protected Card turnCard;

    /** Stores the river card (5th card dealt on the table) */
    protected Card riverCard;

    /** The bet each betting players has to match to stay in the round */
    protected BigInteger currentBet;

    /** All pots in the current hand (main pot and any side pots). */
    private final List<Pot> pots;

    /** Indicates in which round the game is currently in */
    protected BettingRound bettingRound;

    /** The player with the dealer position */
    protected PokerPlayer dealer;

    /** The player with the small-blind position */
    protected PokerPlayer smallBlindPlayer;

    /** The player with the big-blind position */
    protected PokerPlayer bigBlindPlayer;

    /** The player who is currently making an action */
    protected PokerPlayer actor;

    protected ClassFactory<PokerHandEvaluator> evaluatorFactory;

    private PokerPlayer lastBettor;

    private int activePlayers;



    /**
     * Constructor for the poker game
     *
     * @param creator initializes the creator variable with the player which created the game
     * @param buyIn sets the required buy in for the game
     * @param settings sets the settings for the game
     */
    public PokerGame(PokerPlayer creator, BigInteger buyIn, PokerGameSettings settings, TableType tableType,
                     int maxPlayerCount, String name) {
        super(name, creator.getAlias());
        this.raises = 0;
        this.pots = new ArrayList<>();
        this.tableType = tableType;
        this.maxPlayerCount = maxPlayerCount;
        this.buyIn = buyIn;
        this.settings = settings;
        this.playerList = new ArrayList<>(maxPlayerCount);
        this.playerList.add(creator);
        this.deck = new Deck();
        this.evaluatorFactory = new ClassFactory<>(PokerHandEvaluator.class);
    }

    public Player getPlayerById(String playerId) {
        return playerList.stream().filter(s -> s.getId().equals(playerId)).findFirst().orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public void removePlayer(Player player) {
        playerList.remove(player);
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
    @Override
    public void addPlayer(Player player) throws Exception {
        if (this.gameStatus.equals(GameStatus.IN_PROGRESS) || this.gameStatus.equals(GameStatus.FINISHED)) {
            throw new GameActionException("Game is already running");
        }
        if (playerList.contains(player) || playerList.size() >= maxPlayerCount) {
            throw new GameActionException("Player can not join this game.");
        }
        playerList.add(player);
        if(playerList.size() == maxPlayerCount) {
            startGame();
        }
    }


    /**
     * Method to start the game
     *
     * <p>
     *     The game will only be started if at least 3 players have joined the game and the game is not already running
     *     A new deck will be created, the game will be set to running and dealer, small-blind and big-blind positions will be assigned
     * </p>
     */
    public void startGame() throws Exception {
        if (gameStatus.equals(GameStatus.IN_PROGRESS) || gameStatus.equals(GameStatus.FINISHED)) {
            throw new GameActionException("Game is already running");
        }

        if(playerList.size() < 3) {
            throw new GameActionException("Not enough players to start the game");
        }
        this.activePlayers = playerList.size();
        gameStatus = GameStatus.IN_PROGRESS;
        // a random player becomes the first dealer
        dealer = (PokerPlayer) getRandomPlayer();
        actor = dealer;
        smallBlindLevels = BlindHelper.calculateBlindLevels(playerList.size(),
                buyIn, settings.getTotalTournamentTime(), settings.getTimeToRaiseBlinds());
        currentBlindLevel = 0;
        currentBet = BigInteger.ZERO;
    }

    /**
     * Method to deal cards to the players of the game and set blinds
     */
    public void deal() throws Exception {
        if (!gameStatus.equals(GameStatus.IN_PROGRESS)) {
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
              }
            } catch (GameActionException e) {
                throw new RuntimeException(e);
            }
        });

        paySmallBlind();
        payBigBlind();
    }

    private void paySmallBlind() {
        rotateActor();
        smallBlindPlayer = actor;
        BigInteger smallBlind = smallBlindLevels.get(currentBlindLevel);
        smallBlindPlayer.paySmallBlind(smallBlind);
        contributePot(smallBlind);
        currentBet = smallBlind;
    }

    private void payBigBlind() {
        rotateActor();
        bigBlindPlayer = actor;
        BigInteger bigBlind = smallBlindLevels.get(currentBlindLevel).multiply(BigInteger.TWO);
        bigBlindPlayer.payBigBlind(bigBlind);
        contributePot(bigBlind);
        currentBet = bigBlind;
    }

    private void contributePot(BigInteger amount) {
        List<Pot> newPots = new ArrayList<>();
        for (Pot pot : pots) {
            if (!pot.hasContributed(actor)) {
                BigInteger potBet = pot.getBet();
                if (amount.compareTo(potBet) >= 0) {
                    // Regular call, bet, or raise.
                    pot.addContributor(actor);
                    amount = amount.subtract(potBet);
                } else {
                    // Partial call (all-in); create a new pot.
                    Pot newPot = pot.split(actor, amount);
                    newPots.add(newPot);
                    amount = BigInteger.ZERO;
                }
            }
            if (amount.compareTo(BigInteger.ZERO) <= 0) {
                break;
            }
        }

        // Add newly created pots to the original pots list.
        pots.addAll(newPots);

        if (amount.compareTo(BigInteger.ZERO) > 0) {
            Pot pot = new Pot(amount);
            pot.addContributor(actor);
            pots.add(pot);
        }
    }


    /**
     * Method to increase the blind level
     */
    public void raiseBlinds() {
        currentBlindLevel++;
    }


    /**
     * Method to set actor to the next player on the table. It will ignore all folded players or
     * players who are all in
     */
    void rotateActor() {
        if(bettingRound == BettingRound.END) return;

        actor = (PokerPlayer) TurnManager.getNext(playerList, actor, true);
        while (actor.isFolded() || actor.isAllIn() || actor.isSpectator()) {
            actor = (PokerPlayer) TurnManager.getNext(playerList, actor, true);
        }
    }

    /**
     * Method to move the dealer to the next player if the round has ended
     *
     * @param currentDealer the player who is currently the dealer
     */
    public void setNextDealer(PokerPlayer currentDealer) {
        dealer = (PokerPlayer) TurnManager.getNext(playerList, currentDealer, true);
    }

    public boolean isLegalAction(PokerPlayer player, PokerPlayerAction action) throws GameActionException {
        if(!isPlayerTurn(player)) {
            throw new GameActionException("It is not your turn.");
        }
        if (player.isSpectator()) {
            throw new GameActionException("You are not allowed to participate in the game.");
        }
        if (!player.canDoAction(action)) {
            throw new GameActionException("You are not allowed to perform this action.");
        }
        return true;
    }

    /**
     * Method to call a bet
     *
     * @param player the player who is calling
     */
    public void call(PokerPlayer player) throws GameActionException, ReflectiveOperationException {
        if (!isLegalAction(player, PokerPlayerAction.CALL)) {
            return;
        }
        BigInteger bet = currentBet.subtract(player.getCurrentBet());
        if (bet.compareTo(player.getChips()) > 0) {
            throw new GameActionException("You do not have enough chips to call.");
        }
        if (bet.equals(BigInteger.ZERO)) {
            throw new GameActionException("You have already matched the bet.");
        }
        player.call(bet);
        contributePot(bet);

        manageBettingRound();
    }

    /**
     * Method to manage the end of a round
     *
     * <p>
     *     if the round can end, the next round will be started,
     *     else the next player will be set as active player
     * </p>
     */
    void manageBettingRound() throws ReflectiveOperationException {
        if(canRoundEnd()) {
            startNextBettingRound();
        } else {
            rotateActor();
        }
    }

    /**
     * Method to check a bet
     *
     * @param player the player who is checking
     */
    public void check(PokerPlayer player) throws GameActionException, ReflectiveOperationException {
        if (!isLegalAction(player, PokerPlayerAction.CHECK)) {
            return;
        }
        if(player.getCurrentBet().compareTo(currentBet) < 0) {
            throw new GameActionException("Your current bet is too low to check.");
        }

        player.check();

        manageBettingRound();
    }

    /**
     * Method to fold a hand
     *
     * @param player the player that wants to fold
     */
    public void fold(PokerPlayer player) throws GameActionException, ReflectiveOperationException {
        if (!isLegalAction(player, PokerPlayerAction.FOLD)) {
            return;
        }

        player.fold();
        this.activePlayers--;
        manageBettingRound();
    }

    /**
     * Method to raise a bet
     *
     * @param player the player who is raising
     * @param raise the amount the player wants to raise
     */
    public void raise(PokerPlayer player, BigInteger raise) throws GameActionException {
        if (!isLegalAction(player, PokerPlayerAction.RAISE)) {
            return;
        }
        if (tableType.equals(TableType.FIXED_LIMIT) && raises >= MAX_RAISES) {
            throw new GameActionException("Maximum number of raises reached for fixed limit.");
        }
        BigInteger bigBlind = smallBlindLevels.get(currentBlindLevel).multiply(BigInteger.TWO);
        if (raise.subtract(currentBet.subtract(player.getCurrentBet())).compareTo(bigBlind) < 0) {
            throw new GameActionException("Raise is not high enough.");
        }

        if(raise.compareTo(player.getChips()) > 0) {
            throw new GameActionException("You do not have enough chips to raise.");
        }
        raises++;
        lastBettor = player;
        player.bet(raise);
        contributePot(raise);
        currentBet = player.getCurrentBet();
        player.setLastAction(PokerPlayerAction.RAISE);

        rotateActor();
    }

    public void allIn(PokerPlayer player) throws GameActionException {
        if (!isLegalAction(player, PokerPlayerAction.ALL_IN)) {
            return;
        }
        BigInteger allIn = player.getChips();
        player.bet(allIn);
        contributePot(allIn);
        raises++;
        lastBettor = player;
        currentBet = player.getCurrentBet();
        player.setLastAction(PokerPlayerAction.ALL_IN);

        rotateActor();
    }

    private boolean isPlayerTurn(PokerPlayer player) {
        return player.equals(actor);
    }

    /**
     * Method to check whether a round can end
     *
     * @return (true) if only 1 player has not folded -> round will be set to end;
     * (true) if the round is PRE_FLOP, it is the big blinds turn, and all players have called the current bet or have folded
     * (true) if the round is NOT PRE_FLOP, it is the dealers turn, and all players have called the current bet or have folded
     */
    boolean canRoundEnd() {
        if (activePlayers == 1) {
            playerList.stream()
                    .map(player -> (PokerPlayer) player)
                    .filter(player -> !player.isFolded() && !player.isSpectator()).findFirst().get().setHandScore(1000);
            bettingRound = BettingRound.END;
            return true;
        } else if (bettingRound == BettingRound.PRE_FLOP && actor == bigBlindPlayer) {
            return countPlayersThatCalledAndNotFolded() == activePlayers;
        } else if (bettingRound != BettingRound.PRE_FLOP && actor == dealer) {
            return countPlayersThatCalledAndNotFolded() == activePlayers;
        }
        return false;
    }

    private int countPlayersThatCalledAndNotFolded() {
        return (int) playerList.stream()
                .map(player -> (PokerPlayer) player)
                .filter(player -> (player.getCurrentBet().compareTo(currentBet) == 0) && !player.isFolded() && !player.isSpectator()).count();
    }

     /**
      * Method to prepare the game for a new round
      *
      * <p>
      *      NOT a state change, but the whole playing round ended (the pot was won)
      * </p>
      */
    void prepareNextRound() {
        if (playerList.stream().filter(player -> player.getChips().compareTo(BigInteger.ZERO) > 0).count() < 2) {
            gameStatus = GameStatus.FINISHED;
            return;
        }
        setNextDealer(dealer);
        actor = dealer;
        playerList.forEach(Player::reset);
        flopCards.clear();
        riverCard = null;
        lastBettor = null;
        turnCard = null;
        raises = 0;
        currentBet = BigInteger.ZERO;
        pots.clear();
        activePlayers = (int) playerList.stream().map((player -> (PokerPlayer) player))
                .filter(player -> player.getChips().compareTo(BigInteger.ZERO) > 0).count();
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
    void startNextBettingRound() throws ReflectiveOperationException {
        switch (bettingRound) {
            case PRE_FLOP -> {
                bettingRound = BettingRound.FLOP;
                flopCards = new ArrayList<>(3);
                deck.burnCard();
                flopCards.add(deck.drawCard());
                flopCards.add(deck.drawCard());
                flopCards.add(deck.drawCard());
                actor = dealer;
                rotateActor();
            }
            case FLOP -> {
                bettingRound = BettingRound.TURN;
                deck.burnCard();
                turnCard = deck.drawCard();
                actor = dealer;
                rotateActor();
            }
            case TURN -> {
                bettingRound = BettingRound.RIVER;
                deck.burnCard();
                riverCard = deck.drawCard();
                actor = dealer;
                rotateActor();

            }
            case RIVER -> {
                bettingRound = BettingRound.END;
                doShowdown();
            }
        }
    }

    private BigInteger getTotalPot() {
        BigInteger totalPot = BigInteger.ZERO;
        for (Pot pot : pots) {
            totalPot = totalPot.add(pot.getValue());
        }
        return totalPot;
    }

    private List<PokerPlayer> determineShowdownOrder() {
        // Determine show order; start with all-in players...
        List<PokerPlayer> showingPlayers = new ArrayList<>();
        for (Pot pot : pots) {
            for (Player contributor : pot.getContributors()) {
                PokerPlayer player = (PokerPlayer) contributor;
                if (!showingPlayers.contains(player) && player.isAllIn()) {
                    showingPlayers.add(player);
                }
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
            rotateActor();
            if (!showingPlayers.contains(actor)) {
                showingPlayers.add(actor);
            }
        }

        return showingPlayers;
    }

    private void calculateWinning(List<PokerPlayer> showingPlayers) {
        if(bettingRound != BettingRound.END) return;
        // Sort players by hand value (highest to lowest).
        Map<Integer, List<PokerPlayer>> rankedPlayers = new TreeMap<>(Collections.reverseOrder());
        for (PokerPlayer player : showingPlayers) {
            int handValue = player.getHandScore();
            List<PokerPlayer> playerList = rankedPlayers.get(handValue);
            if (playerList == null) {
                playerList = new ArrayList<>();
            }
            playerList.add(player);
            rankedPlayers.put(handValue, playerList);
        }

        // Per rank (single or multiple winners), calculate pot distribution.
        Map<PokerPlayer, BigInteger> potDivision = new HashMap<>();
        for (int handValue : rankedPlayers.keySet()) {
            List<PokerPlayer> winners = rankedPlayers.get(handValue);
            for (Pot pot : pots) {
                // Determine how many winners share this pot.
                int noOfWinnersInPot = 0;
                for (Player winner : winners) {
                    if (pot.hasContributed(winner)) {
                        noOfWinnersInPot++;
                    }
                }
                if (noOfWinnersInPot > 0) {
                    // Divide pot over winners.
                    BigInteger potShare = pot.getValue().divide(new BigInteger(String.valueOf(noOfWinnersInPot)));
                    for (PokerPlayer winner : winners) {
                        if (pot.hasContributed(winner)) {
                            potDivision.merge(winner, potShare, BigInteger::add);
                        }
                    }
                    // Determine if we have any odd chips left in the pot.
                    BigInteger oddChips = pot.getValue().remainder(new BigInteger(String.valueOf(noOfWinnersInPot)));
                    if (oddChips.compareTo(BigInteger.ZERO) > 0) {
                        // Divide odd chips over winners, starting left of the dealer.
                        actor = dealer;
                        while (oddChips.compareTo(BigInteger.ZERO) > 0) {
                            rotateActor();
                            PokerPlayer winner = actor;
                            BigInteger oldShare = potDivision.get(winner);
                            if (oldShare != null) {
                                potDivision.put(winner, oldShare.add(BigInteger.ONE));
                                oddChips = oddChips.subtract(BigInteger.ONE);
                            }
                        }

                    }
                    pot.clear();
                }
            }
        }

        // Divide winnings.
        for (PokerPlayer winner : potDivision.keySet()) {
            BigInteger potShare = potDivision.get(winner);
            winner.win(potShare);
            winner.setPotShare(potShare);
        }
    }

    /**
     * Performs the showdown.
     */
    private void doShowdown() throws ReflectiveOperationException {
        if(bettingRound != BettingRound.END) return;

        List<PokerPlayer> showingPlayers = determineShowdownOrder();
        int bestHandValue = -1;
        for (PokerPlayer playerToShow : showingPlayers) {
            evaluateHand(playerToShow);
            if (playerToShow.isAllIn()) {
                playerToShow.setMustShowCards(true);
            }
            // players other than all-in players only have to show when having a chance to win
            if (bestHandValue <= playerToShow.getHandScore()) {
                bestHandValue = playerToShow.getHandScore();
                playerToShow.setMustShowCards(true);
            }
        }

        calculateWinning(showingPlayers);

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
    public void evaluateHand(PokerPlayer player) throws ReflectiveOperationException {
        PokerHandEvaluator handEvaluator = evaluatorFactory.createInstance(
                player.getHand().get(0),
                player.getHand().get(1),
                flopCards.get(0),
                flopCards.get(1),
                flopCards.get(2),
                riverCard,
                turnCard);

        player.setHandScore(handEvaluator.bestHand());
    }



}
