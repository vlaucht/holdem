package de.thm.holdem.service;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.dto.GameActionRequest;
import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.dto.PokerGameStateDto;
import de.thm.holdem.exception.ApiError;
import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.exception.NotFoundException;
import de.thm.holdem.model.game.poker.PokerGame;

/**
 * Service Interface for business logic regarding poker games.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public interface PokerGameService {

    /**
     * Creates a new poker game.
     *
     * @param player the player who creates the game
     * @param request the request containing the game settings
     * @return the created game
     * @throws Exception if the game could not be created
     */
    PokerGame createGame(String player, PokerGameCreateRequest request) throws Exception;

    /** Removes a player from the game. If no players left in the game, the game will be deleted.
     *
     * <p> Broadcasts the current game state to all players in the game and notifies the game lobby.
     *
     * @param gameId the id of the game
     * @param playerId the id of the player
     */
    void leaveGame(String gameId, String playerId) throws NotFoundException;

    /**
     * Broadcasts the current game state to all players in the game.
     *
     * @param game the game to broadcast
     */
    void broadcastGameState(PokerGame game, ClientOperation operation);

    /**
     * Get a game by its id.
     *
     * @param gameId the id of the game.
     * @return the game.
     * @throws NotFoundException if the game could not be found.
     */
    PokerGame getGame(String gameId) throws NotFoundException;

    /**
     * Checks if a player is in a game.
     *
     * @param game the game to check.
     * @param playerId the id of the player.
     * @return true if the player is in the game, false otherwise.
     */
    boolean isPlayerInGame(PokerGame game, String playerId);

    /**
     * Joins a user to a game, manages the buy-in and notifies the players and lobby.
     *
     * @param gameId the id of the game.
     * @param userId the id of the user.
     * @return the game.
     * @throws Exception if the user could not be joined to the game.
     */
    PokerGame joinGame(String gameId, String userId) throws Exception;

    void startGame(String gameId, String playerId) throws Exception;

    PokerGameStateDto mergePrivateInfo(PokerGame game, String playerId);

    void performAction(GameActionRequest request, String playerId);

    void sendErrorMessage(String playerId, ApiError error);
}
