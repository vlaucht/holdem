package de.thm.holdem.service;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.dto.PokerGameLobbyDto;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.user.UserExtra;

import java.util.List;

/**
 * Service for the game lobby.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public interface GameLobbyService {

    /**
     * Returns all poker games in the lobby.
     *
     * @return list of all {@link de.thm.holdem.model.game.poker.PokerGame}
     */
    List<PokerGameLobbyDto> getPokerGames();

    /**
     * Broadcasts a game update to all clients connected to the lobby.
     *
     * @param game the game that should be updated
     * @param operation the instruction to the client
     */
    void broadcast(PokerGame game, ClientOperation operation);

    /**
     * Broadcasts all games to all clients connected to the lobby.
     */
    void broadcastAll();


}
