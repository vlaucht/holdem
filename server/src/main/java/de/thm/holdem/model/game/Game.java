package de.thm.holdem.model.game;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.model.player.Player;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
public abstract class Game {

    /** Unique identifier of the game */
    protected String id;

    /** Name of the game */
    protected String name;

    /** Current {@link GameStatus} of the game */
    protected GameStatus gameStatus;

    /** List of players in the game */
    protected ArrayList<Player> playerList;

    /** List of listeners for the game, used to send notifications. */
    protected List<GameListener> listeners = new ArrayList<>();

    /** Time of creation */
    private final LocalDate creationDate;

    /** Alias of the player who created the game */
    private final String creator;

    /**
     * Constructor for a game.
     *
     * @param name name of the game.
     * @param creator alias of the player who created the game.
     */
    protected Game(String name, String creator) {
        this.name = name;
        this.creator = creator;
        this.creationDate = LocalDate.now();
        this.gameStatus = GameStatus.WAITING;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Remove the player from the game.
     *
     * @param player the player to remove
     */
    abstract public void removePlayer(Player player);

    /**
     * Add a player to the game.
     *
     * @param player the player to add.
     * @throws Exception if the player could not be added.
     */
    abstract public void addPlayer(Player player) throws Exception;

    /**
     * Add a listener to the game.
     *
     * @param listener the listener to add.
     */
    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener from the game.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify all listeners about a change and send private notifications to all players.
     *
     * @param operation the {@link ClientOperation} to add to the message.
     */
    abstract protected void notifyPlayers(ClientOperation operation);

    /**
     * Notify a single player about a change in his private information.
     *
     * @param player the player to notify.
     */
    abstract protected void notifyPlayer(Player player);

    /**
     * Notify all listeners about a change in the game state.
     *
     * @param operation the {@link ClientOperation} to add to the message.
     */
    abstract protected void notifyGameState(ClientOperation operation);

    /**
     * Method to get a random player from the player list.
     *
     * @return a random player.
     */
    public Player getRandomPlayer() {
        if (playerList == null || playerList.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(playerList.size());
        return playerList.get(randomIndex);
    }
}
