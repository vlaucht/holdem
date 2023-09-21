package de.thm.holdem.service;

import de.thm.holdem.model.user.UserExtra;
import de.thm.holdem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService  {

    private final UserRepository userExtraRepository;
    private final AvatarService avatarService;

    @Value("${player.initial-bankroll}")
    private String initialBankroll;

    @Value("${avatar.api.url}")
    private String apiUrl;

    @Override
    public UserExtra validateAndGetUserExtra(String username) {
        Optional<UserExtra> userExtra = getUserExtra(username);
        if (userExtra.isPresent()) {
            return userExtra.get();
        }
        UserExtra newUserExtra = new UserExtra(username);
        String avatar = avatarService.getRandomAvatarUrl();
        newUserExtra.setAvatar(avatar);
        newUserExtra.setBankroll(Integer.parseInt(initialBankroll));
        return saveUserExtra(newUserExtra);
    }

    @Override
    public Optional<UserExtra> getUserExtra(String username) {
        return userExtraRepository.findById(username);
    }

    @Override
    public UserExtra saveUserExtra(UserExtra userExtra) {
        return userExtraRepository.save(userExtra);
    }
}
