package de.thm.holdem.model.game;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.model.player.Player;

import java.util.List;

public interface GameListener {

    /**
     * Notify all players about a change in the players public information.
     *
     * @param game the game the players are in
     */
    void onNotifyPlayers(Game game, ClientOperation operation);

    /**
     * Notify a single player about a change in his private information.
     *
     * <p>
     *     Method is used to update a single player about his private information from within the game.
     * </p>
     *
     * @param player the player to notify
     * @param game the game the player is in
     * @param payload the payload to send to the player
     * @param <T> the type of the payload
     */
    <T> void onNotifyPlayer(Player player, Game game, T payload);

    /**
     * Notify all listeners about a change in the game state.
     *
     * <p>
     *     Method is used to allow broadcasting game changes from within the game.
     * </p>
     *
     * @param game the game that changed.
     * @param operation instruction for the client what to do with the change.
     */
    void onNotifyGameState(Game game, ClientOperation operation);
}
