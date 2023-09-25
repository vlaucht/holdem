package de.thm.holdem.service;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.springframework.stereotype.Service;

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

    /** Bidirectional map of user ids to session ids. */
    private final BiMap<String, String> connections = HashBiMap.create();

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
     * @return the user id of the disconnected user
     */
    public String disconnect(String sessionId) {
        return connections.inverse().remove(sessionId);
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
