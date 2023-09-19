package de.thm.holdem.model.game;

import de.thm.holdem.model.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public abstract class Game {

    protected GameStatus gameStatus;

    protected ArrayList<Player> playerList;
}
