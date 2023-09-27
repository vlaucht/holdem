package de.thm.holdem.service;

/**
 * Service for all websocket related actions.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public interface WebsocketService {

    /**
     * Broadcasts a payload to all clients in a room.
     *
     * @param room The room to broadcast to.
     * @param payload The payload to send.
     * @param <T> The type of the payload.
     */
    <T> void broadcast(String room, T payload);

    /**
     * Sends a payload to a specific user.
     *
     * @param userId The id of the user to send to.
     * @param channel The channel to send to.
     * @param payload The payload to send.
     * @param <T> The type of the payload.
     */
    <T> void sendPrivate(String userId, String channel, T payload);

    <T> void sendPrivateToSession(String sessionId, String channel, T payload);
}
