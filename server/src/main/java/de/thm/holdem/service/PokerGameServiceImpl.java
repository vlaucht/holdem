package de.thm.holdem.service;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.dto.PokerGameStateDto;
import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.exception.NotFoundException;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.player.Player;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.model.user.UserExtra;
import de.thm.holdem.settings.PokerGameSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class PokerGameServiceImpl implements PokerGameService {

    private final PokerGameSettings settings;

    private final PokerGameRegistry registry;

    private final UserService userService;

    private final GameLobbyService gameLobbyService;

    private final WebsocketService websocketService;


    /** {@inheritDoc} */
    public PokerGame createGame(String playerId, PokerGameCreateRequest request) throws Exception {
        UserExtra userExtra = userService.getUserExtra(playerId);

        if (userExtra.getBankroll().compareTo(BigInteger.valueOf(request.getBuyIn())) < 0) {
            throw new GameActionException("Not enough cash to create the game.");
        }

        PokerPlayer pokerPlayer = new PokerPlayer(playerId, userExtra.getUsername(), userExtra.getAvatar(),
                userExtra.getBankroll());
        PokerGame game = new PokerGame(pokerPlayer, BigInteger.valueOf(request.getBuyIn()),
                settings, request.getTableType(), request.getMaxPlayerCount(), request.getName());
        userExtra.setBankroll(pokerPlayer.joinGame(BigInteger.valueOf(request.getBuyIn())));
        registry.addGame(game);
        userExtra.setActiveGameId(game.getId());
        userService.playGame(userExtra);
        gameLobbyService.notifyBankrollChange(userExtra);
        gameLobbyService.broadcast(game, ClientOperation.CREATE);
        return game;
    }


    public PokerGame joinGame(String gameId, String userId) throws NotFoundException, GameActionException {
        PokerGame game = registry.getGame(gameId);
        if (game == null) {
            throw new NotFoundException("Game not found");
        }
        UserExtra userExtra = userService.getUserExtra(userId);
        if (userExtra.getBankroll().compareTo(game.getBuyIn()) < 0) {
            throw new GameActionException("Not enough cash to join the game.");
        }
        PokerPlayer pokerPlayer = new PokerPlayer(userId, userExtra.getUsername(), userExtra.getAvatar(),
                userExtra.getBankroll());
        userExtra.setBankroll(pokerPlayer.joinGame(game.getBuyIn()));
        userExtra.setActiveGameId(game.getId());
        userService.playGame(userExtra);
        gameLobbyService.notifyBankrollChange(userExtra);
        gameLobbyService.broadcast(game, ClientOperation.UPDATE);
        broadcastGameState(game);
        return game;
    }

    /** {@inheritDoc} */
    public void leaveGame(String gameID, String playerId) throws NotFoundException {
        PokerGame game = registry.getGame(gameID);
        Player player = game.getPlayerById(playerId);

        if (player == null) {
            throw new NotFoundException("Player not found");
        }

        BigInteger bankroll = player.leaveGame();
        game.removePlayer(player);

        userService.leaveGame(playerId, bankroll);

        if (game.getPlayerList().size() == 0) {
            registry.removeGame(gameID);
            gameLobbyService.broadcast(game, ClientOperation.DELETE);
        } else {
            gameLobbyService.broadcast(game, ClientOperation.UPDATE);
            broadcastGameState(game);
        }
    }

    /** {@inheritDoc} */
    public void broadcastGameState(PokerGame game) {
        websocketService.broadcast("/topic/game/" + game.getId(), PokerGameStateDto.from(game));
    }

    public PokerGame getGame(String gameId) throws NotFoundException {
        if (!registry.containsGame(gameId)) {
            throw new NotFoundException("Game not found");
        }
        return registry.getGame(gameId);
    }

    @Override
    public boolean isPlayerInGame(PokerGame game, String playerId) {
        boolean playerExists = false;
        for (Player player : game.getPlayerList()) {
            if (player.getId().equals(playerId)) {
                playerExists = true;
                break;
            }
        }
        return playerExists;
    }

}
