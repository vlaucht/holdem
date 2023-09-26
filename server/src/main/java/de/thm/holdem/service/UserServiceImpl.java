package de.thm.holdem.service;

import de.thm.holdem.exception.UserNotFoundException;
import de.thm.holdem.model.user.UserExtra;
import de.thm.holdem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Implementation of the {@link UserService}.
 *
 * @see UserService
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService  {

    private final UserRepository userExtraRepository;

    private final AvatarService avatarService;

    private final PokerGameRegistry pokerGameRegistry;

    private final WebsocketService websocketService;

    @Value("${player.initial-bankroll}")
    protected String initialBankroll;


    /**
     * Get the {@link UserExtra} for the given username.
     *
     * <p>
     *     If the {@link UserExtra} already exists, it will be returned.
     *     Otherwise a new {@link UserExtra} will be created.
     * </p>
     *
     * @param username the username of the user
     * @return the {@link UserExtra} for the given username
     */
    @Override
    public UserExtra getUserExtra(String id, String username) {
        Optional<UserExtra> userExtra = userExtraRepository.findById(id);
        if (userExtra.isPresent()) {
            checkUserActiveGame(userExtra.get());
            return userExtra.get();
        }
        UserExtra newUserExtra = new UserExtra(id, username);
        String avatar = avatarService.getRandomAvatarUrl();
        newUserExtra.setAvatar(avatar);
        newUserExtra.setBankroll(new BigInteger(initialBankroll));
        return userExtraRepository.save(newUserExtra);
    }

    /**
     * Check if the user has active games and remove him from them if they do not exist anymore.
     *
     * <p>
     *     This can only happen if the server gets shut down while the user is in a game.
     * </p>
     *
     * @param userExtra the user to check
     */
    private void checkUserActiveGame(UserExtra userExtra) {
        if (userExtra.getActiveGameId() != null) {
            if (!pokerGameRegistry.containsGame(userExtra.getActiveGameId())) {
                userExtra.setActiveGameId(null);
                userExtraRepository.save(userExtra);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public UserExtra getUserExtra(String id) {
        Optional<UserExtra> userExtra = userExtraRepository.findById(id);
        return userExtra.orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    /** {@inheritDoc} */
    @Override
    public UserExtra saveUserExtra(UserExtra userExtra) {
        return userExtraRepository.save(userExtra);
    }

    /** {@inheritDoc} */
    @Override
    public UserExtra recharge(String id) {
        UserExtra userExtra = getUserExtra(id);
        userExtra.setBankroll(new BigInteger(initialBankroll));
        return userExtraRepository.save(userExtra);
    }

    /** {@inheritDoc} */
    @Override
    public UserExtra playGame(String id, String gameId) {
        UserExtra userExtra = getUserExtra(id);
        userExtra.setActiveGameId(gameId);
        return userExtraRepository.save(userExtra);
    }

    /** {@inheritDoc} */
    @Override
    public UserExtra playGame(UserExtra userExtra) {
        notifyUserUpdate(userExtra);
        return userExtraRepository.save(userExtra);
    }

    /** {@inheritDoc} */
    @Override
    public UserExtra leaveGame(String id, BigInteger bankroll) {
        UserExtra userExtra = getUserExtra(id);
        userExtra.setBankroll(bankroll);
        userExtra.setActiveGameId(null);
        notifyUserUpdate(userExtra);
        return userExtraRepository.save(userExtra);
    }

    public void notifyUserUpdate(UserExtra userExtra) {
        websocketService.sendPrivate(userExtra.getId(), "user-extra", userExtra);
    }

}
