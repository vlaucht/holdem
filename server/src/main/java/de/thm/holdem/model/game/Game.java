package de.thm.holdem.model.game;

import de.thm.holdem.model.player.Player;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
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
     * Remove the player from the game and return his remaining chips that are not currently in a bet.
     *
     * @param id the id of the player to remove
     * @return the remaining chips of the player
     */
    abstract public BigInteger removePlayer(String id);

    abstract public void addPlayer(Player player);
}
