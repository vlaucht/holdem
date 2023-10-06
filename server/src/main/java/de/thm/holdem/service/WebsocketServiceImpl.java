package de.thm.holdem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link WebsocketService}.
 * A service for all websocket related actions.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class WebsocketServiceImpl implements WebsocketService {

    private final ConnectionRegistry connectionRegistry;
    private final SimpMessagingTemplate template;

    /** {@inheritDoc} */
    @Override
    public <T> void broadcast(String room, T payload) {
        template.convertAndSend(room, payload);
    }

    /** {@inheritDoc} */
    @Override
    public <T> void sendPrivate(String userId, String channel, T payload) {
        if (!connectionRegistry.isConnected(userId)) {
            return;
        }
        String sessionId = connectionRegistry.getConnection(userId);
        String destination = "/queue/" + channel;
        template.convertAndSendToUser(sessionId, destination, payload,
                createHeaders(sessionId));

    }

    @Override
    public <T> void sendPrivateToSession(String sessionId, String channel, T payload) {
        String destination = "/queue/" + channel;
        template.convertAndSendToUser(sessionId, destination, payload,
                createHeaders(sessionId));
    }

    /**
     * Creates the headers for a private message.
     *
     * @param sessionId the session id of the user
     * @return the headers for the message
     */
    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}
