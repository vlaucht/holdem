package de.thm.holdem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsocketServiceImpl implements WebsocketService {

    private final SimpMessagingTemplate template;
    @Override
    public <T> void broadcast(String room, T payload) {
        template.convertAndSend(room, payload);
    }
}
