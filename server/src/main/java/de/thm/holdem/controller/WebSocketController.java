package de.thm.holdem.controller;

import de.thm.holdem.service.ConnectionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ConnectionRegistry registry;
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        registry.disconnect(sessionId);
    }

    @EventListener
    public void handleSessionConnect(SessionConnectedEvent event) {
        System.out.println(event.getMessage().getHeaders());
    }

}
