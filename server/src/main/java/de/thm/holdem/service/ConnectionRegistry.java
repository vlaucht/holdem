package de.thm.holdem.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry to keep track of user connections.
 *
 * <p>This is used to send messages to specific users.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Service
public class ConnectionRegistry {

    /** Map of user ids to session ids. */
    private final Map<String, String> connections = new ConcurrentHashMap<>();

    /**
     * Connects a user to a session.
     *
     * @param userId the user id
     * @param sessionId the session id
     */
    public void connect(String userId, String sessionId) {
        connections.put(userId, sessionId);
    }

    /**
     * Disconnects a user from a session.
     *
     * @param sessionId the session id
     */
    public void disconnect(String sessionId) {
        connections.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
    }

    /**
     * Gets the session id of a user.
     *
     * @param userId the user id
     * @return the session id
     */
    public String getConnection(String userId) {
        return connections.get(userId);
    }

    /**
     * Checks if a user is connected.
     *
     * @param userId the user id
     * @return true if the user is connected, false otherwise
     */
    public boolean isConnected(String userId) {
        return connections.containsKey(userId);
    }
}
