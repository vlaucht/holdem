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
            return userExtra.get();
        }
        UserExtra newUserExtra = new UserExtra(id, username);
        String avatar = avatarService.getRandomAvatarUrl();
        newUserExtra.setAvatar(avatar);
        newUserExtra.setBankroll(new BigInteger(initialBankroll));
        return userExtraRepository.save(newUserExtra);
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
        return userExtraRepository.save(userExtra);
    }

    /** {@inheritDoc} */
    @Override
    public UserExtra leaveGame(String id, BigInteger remainingChips) {
        UserExtra userExtra = getUserExtra(id);
        userExtra.setBankroll(userExtra.getBankroll().add(remainingChips));
        userExtra.setActiveGameId(null);
        return userExtraRepository.save(userExtra);
    }

}
