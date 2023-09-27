package de.thm.holdem.service;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.dto.GameActionRequest;
import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.dto.PokerGameStateDto;
import de.thm.holdem.exception.ApiError;
import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.exception.NotFoundException;
import de.thm.holdem.model.game.poker.PokerGame;


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

    PokerGame getGame(String gameId) throws NotFoundException;

    boolean isPlayerInGame(PokerGame game, String playerId);

    PokerGame joinGame(String gameId, String userId) throws Exception;

    void startGame(String gameId, String playerId) throws Exception;

    PokerGameStateDto mergePrivateInfo(PokerGame game, String playerId);

    void performAction(GameActionRequest request, String playerId);

    void sendErrorMessage(String playerId, ApiError error);
}
