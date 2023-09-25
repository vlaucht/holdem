package de.thm.holdem.service;

import de.thm.holdem.model.user.UserExtra;

import java.math.BigInteger;

/**
 * Service for the {@link UserExtra}.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public interface UserService {

    /**
     * Get the {@link UserExtra} for the given id.
     *
     * @param id the id of the user
     * @param username the username of the user
     * @return the {@link UserExtra} for the given username
     */
    UserExtra getUserExtra(String id, String username);

    /**
     * Get the {@link UserExtra} for the given id.
     *
     * @param id the id of the user
     * @return the {@link UserExtra} for the given id
     */
    UserExtra getUserExtra(String id);

    /**
     * Save the {@link UserExtra} to the database.
     *
     * @param userExtra the {@link UserExtra} to save
     * @return the saved {@link UserExtra}
     */
    UserExtra saveUserExtra(UserExtra userExtra);

    /**
     * Recharge the bankroll of the user with the initial bankroll.
     *
     * @param username the username of the user
     * @return the updated {@link UserExtra}
     */
    UserExtra recharge(String username);

    /**
     * Set the active game of the user.
     *
     * @param id the id of the user
     * @param gameId the id of the game
     * @return the updated {@link UserExtra}
     */
    UserExtra playGame(String id, String gameId);

    /**
     * Set the active game of the user and update the bankroll.
     *
     * @param userExtra the userExtra with the updated values
     * @return the saved {@link UserExtra}
     */
    UserExtra playGame(UserExtra userExtra);

    /**
     * Remove the active game of the user.
     *
     * @param id the id of the user
     * @param remainingChips the remaining chips of the user to be added to the bankroll
     * @return the updated {@link UserExtra}
     */
    UserExtra leaveGame(String id, BigInteger remainingChips);
}
