package de.thm.holdem.service;

import de.thm.holdem.exception.NotFoundException;
import de.thm.holdem.model.game.poker.PokerGame;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a registry that stores all currently active poker games.
 *
 * @see PokerGame
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Service
public class PokerGameRegistry implements GameRegistry<PokerGame> {

    /** Hash map to store all currently active poker games. */
    private final Map<String, PokerGame> games = new HashMap<>();

    /** {@inheritDoc} */
    public PokerGame getGame(String id) throws NotFoundException {
        if (!containsGame(id)) {
            throw new NotFoundException(String.format("Game with id %s not found.", id));
        }
        return games.get(id);
    }

    /** {@inheritDoc} */
    public void addGame(PokerGame game) {
        games.put(game.getId(), game);
    }

    /** {@inheritDoc} */
    public void removeGame(String id) {
        games.remove(id);
    }

    /** {@inheritDoc} */
    public boolean containsGame(String id) {
        return games.containsKey(id);
    }

    /** {@inheritDoc} */
    public void clear() {
        games.clear();
    }

    /** {@inheritDoc} */
    public int size() {
        return games.size();
    }

    /** {@inheritDoc} */
    public List<PokerGame> getGames() {
        return new ArrayList<>(games.values());
    }
}
