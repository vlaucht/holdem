package de.thm.holdem.service;

import de.thm.holdem.exception.NotFoundException;

import java.util.List;

public interface GameRegistry<T> {

    /**
     * Returns the game with the given id.
     *
     * @param id The id of the game.
     * @return The game with the given id.
     * @throws NotFoundException If no game with the given id exists.
     */
    T getGame(String id) throws NotFoundException;

    /**
     * Adds a game to the registry.
     *
     * @param game The game to add.
     */
    void addGame(T game);

    /**
     * Removes a game from the registry.
     *
     * @param id The id of the game to remove.
     */
    void removeGame(String id);

    /**
     * Checks if a game with the given id exists.
     *
     * @param id The id of the game to check.
     * @return True if a game with the given id exists, false otherwise.
     */
    boolean containsGame(String id);

    /**
     * Removes all games from the registry.
     */
    void clear();

    /**
     * Returns the number of games in the registry.
     *
     * @return The number of games in the registry.
     */
    int size();

    /**
     * Returns a list of all games in the registry.
     *
     * @return A list of all games in the registry.
     */
    List<T> getGames();
}
