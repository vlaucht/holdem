package de.thm.holdem.service;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.exception.GameActionException;
import de.thm.holdem.model.game.poker.PokerGame;
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


    public PokerGame createGame(String player, PokerGameCreateRequest request) throws Exception {
        UserExtra userExtra = userService.getUserExtra(player);

        if (userExtra.getBankroll().compareTo(BigInteger.valueOf(request.getBuyIn())) < 0) {
            throw new GameActionException("Not enough cash to create the game.");
        }

        PokerPlayer pokerPlayer = new PokerPlayer(player, userExtra.getAvatar(), userExtra.getBankroll());
        PokerGame game = new PokerGame(pokerPlayer, BigInteger.valueOf(request.getBuyIn()),
                settings, request.getTableType(), request.getMaxPlayerCount(), request.getName());
        userExtra.setBankroll(pokerPlayer.joinGame(BigInteger.valueOf(request.getBuyIn())));
        registry.addGame(game);
        userService.saveUserExtra(userExtra);
        // TODO notify lobby and notify creator because of bankroll change
        gameLobbyService.broadcast(game, ClientOperation.CREATE);
        return game;
    }



    public PokerGame joinGame(String gameID, String player) throws Exception {
        return null;
    }

    public void leaveGame(String gameID, String player) throws Exception {
        PokerGame game = registry.getGame(gameID);
        // TODO leave, if no players left, delete game
        return;
    }

}
