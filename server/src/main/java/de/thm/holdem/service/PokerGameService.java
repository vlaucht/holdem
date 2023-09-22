package de.thm.holdem.service;

import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.model.game.poker.PokerGame;


public interface PokerGameService {

    PokerGame createGame(String player, PokerGameCreateRequest request) throws Exception;

}
