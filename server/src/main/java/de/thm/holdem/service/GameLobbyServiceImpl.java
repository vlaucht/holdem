package de.thm.holdem.service;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.dto.PokerGameLobbyDto;
import de.thm.holdem.model.game.poker.PokerGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameLobbyServiceImpl implements GameLobbyService {

    private final WebsocketService websocketService;

    private final PokerGameRegistry registry;

    public List<PokerGameLobbyDto> getPokerGames() {
        return registry.getGames().stream().map(PokerGameLobbyDto::from).toList();
    }

    public void broadcastAll() {
        websocketService.broadcast("/topic/lobby", getPokerGames());
    }

    public void broadcast(PokerGame game, ClientOperation operation) {
        websocketService.broadcast("/topic/lobby", PokerGameLobbyDto.from(game, operation));
    }
}
