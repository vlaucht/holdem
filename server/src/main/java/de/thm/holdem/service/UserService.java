package de.thm.holdem.service;

import de.thm.holdem.model.user.UserExtra;

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
}
