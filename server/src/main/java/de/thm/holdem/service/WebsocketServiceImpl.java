package de.thm.holdem.service;

import lombok.RequiredArgsConstructor;
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

    private final SimpMessagingTemplate template;

    /** {@inheritDoc} */
    @Override
    public <T> void broadcast(String room, T payload) {
        template.convertAndSend(room, payload);
    }
}
