package de.thm.holdem.service;

import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.settings.PokerGameSettings;
import org.springframework.stereotype.Service;

@Service
public class PokerGameServiceImpl implements PokerGameService {

    private final PokerGameSettings settings;


    public PokerGameServiceImpl(PokerGameSettings settings) {
        this.settings = settings;
    }

    public PokerGame createGame(PokerPlayer player, int buyIn) {
        PokerGame game = new PokerGame(player, buyIn, settings);
        return null;
    }

    public PokerGame joinGame(String gameID, String player) throws Exception {
        return null;
    }

    public void leaveGame(String gameID, String player) throws Exception {
        // TODO leave, if no players left, delete game
        return;
    }
}
