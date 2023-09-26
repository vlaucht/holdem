package de.thm.holdem.model.game;

import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.model.player.Player;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
public abstract class Game {

    protected String id;

    protected String name;

    protected GameStatus gameStatus;

    protected ArrayList<Player> playerList;

    /** Time of creation */
    private final LocalDate creationDate;

    /** Alias of the player who created the game */
    private final String creator;

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

    abstract public void addPlayer(Player player) throws Exception;

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
