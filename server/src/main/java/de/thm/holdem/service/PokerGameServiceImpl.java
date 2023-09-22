package de.thm.holdem.service;

import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.settings.PokerGameSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokerGameServiceImpl implements PokerGameService {

    private final PokerGameSettings settings;

    private final PokerGameRegistry registry;


    public PokerGame createGame(PokerPlayer player, int buyIn) {
        PokerGame game = new PokerGame(player, buyIn, settings);
        registry.addGame(game);
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
