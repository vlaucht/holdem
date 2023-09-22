package de.thm.holdem.model.game;

import de.thm.holdem.model.player.Player;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public abstract class Game {

    protected String id;

    protected String name;

    protected GameStatus gameStatus;

    protected ArrayList<Player> playerList;
}
