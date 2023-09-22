package de.thm.holdem.service;

import de.thm.holdem.dto.ClientOperation;
import de.thm.holdem.dto.PokerGameLobbyDto;
import de.thm.holdem.model.game.poker.PokerGame;

import java.util.List;

public interface GameLobbyService {

    List<PokerGameLobbyDto> getPokerGames();

    void broadcast(PokerGame game, ClientOperation operation);

    void broadcastAll();
}
