package de.thm.holdem.service;

import de.thm.holdem.dto.*;
import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.exception.NotFoundException;
import de.thm.holdem.model.game.Game;
import de.thm.holdem.model.game.GameListener;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.game.poker.PokerPlayerAction;
import de.thm.holdem.model.player.Player;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.model.user.UserExtra;
import de.thm.holdem.settings.PokerGameSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PokerGameServiceImpl implements PokerGameService, GameListener {

    private final PokerGameSettings settings;

    private final PokerGameRegistry registry;

    private final UserService userService;

    private final GameLobbyService gameLobbyService;

    private final WebsocketService websocketService;


    /**
     * {@inheritDoc}
     */
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
        userService.notifyUserUpdate(userExtra);
        gameLobbyService.broadcast(game, ClientOperation.CREATE);
        game.addListener(this);
        return game;
    }

    public PokerGameStateDto mergePrivateInfo(PokerGame game, String playerId) {
        PokerPlayer player = (PokerPlayer) game.getPlayerById(playerId);
        int playerIndex = game.getPlayerList().indexOf(player);
        PokerGameStateDto gameStateDto = PokerGameStateDto.from(game);
        PokerPlayerStateDto privateInfo = PokerPlayerStateDto.from(player, game, true);
        if (playerIndex != -1) {
            List<PokerPlayerStateDto> players = new ArrayList<>(gameStateDto.getPlayers());
            players.set(playerIndex, privateInfo);
            gameStateDto.setPlayers(players);
        }
        return gameStateDto;
    }

    public void performAction(GameActionRequest request, String playerId) {
        String action = request.getAction();
        PokerPlayerAction pokerAction = PokerPlayerAction.fromString(action);
        // Get the game
        if (pokerAction != null) {
            try {
                PokerGame game = registry.getGame(request.getGameId());
                PokerPlayer player = (PokerPlayer) game.getPlayerById(playerId);
                if (player == null) {
                    throw new NotFoundException("Player not found");
                }
                switch (pokerAction) {
                    case FOLD -> game.fold(player);
                    case CHECK -> game.check(player);
                    case CALL -> game.call(player);
                    case RAISE -> game.raise(player, BigInteger.valueOf(request.getAmount()));
                    case ALL_IN -> game.allIn(player);
                    default -> {
                    }
                }
            } catch (Exception e) {
                // Handle GameActionException
            }
        } else {
            // Handle unexpected action
        }
    }


    public PokerGame joinGame(String gameId, String userId) throws Exception {
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
        game.addPlayer(pokerPlayer);
        userExtra.setBankroll(pokerPlayer.joinGame(game.getBuyIn()));
        userExtra.setActiveGameId(game.getId());
        userService.playGame(userExtra);
        userService.notifyUserUpdate(userExtra);
        gameLobbyService.broadcast(game, ClientOperation.UPDATE);
        broadcastGameState(game, ClientOperation.JOIN_PLAYER);
        return game;
    }

    @Override
    public void startGame(String gameId, String playerId) throws Exception {
        PokerGame game = registry.getGame(gameId);
        if (game == null) {
            throw new NotFoundException("Game not found");
        }
        // only creator should be able to start the game, but if he leaves, there should be a backup
        if (!game.getPlayerList().get(0).getId().equals(playerId)) {
            throw new GameActionException("You can not start this game.");
        }
        game.startGame();
        gameLobbyService.broadcast(game, ClientOperation.UPDATE);
        // broadcastGameState(game, ClientOperation.START_GAME);
    }

    /**
     * {@inheritDoc}
     */
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
            broadcastGameState(game, ClientOperation.LEAVE_PLAYER);
        }
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

    @Override
    public void onNotifyPlayers(Game game, ClientOperation operation) {
        for (Player player : game.getPlayerList()) {
            sendPrivateInfo((PokerPlayer) player, (PokerGame) game, game.getId());
        }
    }

    @Override
    public <T> void onNotifyPlayer(Player player, Game game, T payload) {

    }

    @Override
    public void onNotifyGameState(Game game, ClientOperation operation) {
        broadcastGameState((PokerGame) game, operation);
    }

    /**
     * {@inheritDoc}
     */
    public void broadcastGameState(PokerGame game, ClientOperation operation) {
        websocketService.broadcast("/topic/game/" + game.getId(), PokerGameStateDto.from(game, operation));
    }

    public void sendPrivateInfo(PokerPlayer player, PokerGame game, String gameId) {
        websocketService.sendPrivate(player.getId(), gameId + "/private-info", PokerPlayerStateDto.from(player, game, true));
    }
}
