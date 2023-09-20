package de.thm.holdem.service;

import de.thm.holdem.model.game.PokerGame;
import de.thm.holdem.model.player.PokerPlayer;
import de.thm.holdem.settings.PokerGameSettings;
import org.springframework.stereotype.Service;

@Service
public class PokerGameService {

    private final PokerGameSettings settings;

    public PokerGameService(PokerGameSettings settings) {
        this.settings = settings;
    }

    public PokerGame createGame(PokerPlayer player, int buyIn) {
        return new PokerGame(player, buyIn, settings);
    }
}
