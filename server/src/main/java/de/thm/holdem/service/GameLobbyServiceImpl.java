package de.thm.holdem.service;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.dto.PokerGameLobbyDto;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.user.UserExtra;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link de.thm.holdem.service.GameLobbyService}.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameLobbyServiceImpl implements GameLobbyService {

    /** the service for websocket communication */
    private final WebsocketService websocketService;

    /** the registry of all poker games */
    private final PokerGameRegistry registry;

    /** {@inheritDoc} */
    public List<PokerGameLobbyDto> getPokerGames() {
        return registry.getGames().stream().map(PokerGameLobbyDto::from).toList();
    }

    /** {@inheritDoc} */
    public void broadcastAll() {
        websocketService.broadcast("/topic/lobby", getPokerGames());
    }

    /** {@inheritDoc} */
    public void broadcast(PokerGame game, ClientOperation operation) {
        websocketService.broadcast("/topic/lobby", PokerGameLobbyDto.from(game, operation));
    }

    /** {@inheritDoc} */
    public void notifyBankrollChange(UserExtra userExtra) {
        websocketService.sendPrivate(userExtra.getId(), "bankroll", userExtra.getBankroll());
    }
}
