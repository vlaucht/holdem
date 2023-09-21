package de.thm.holdem.service;

import de.thm.holdem.model.user.UserExtra;

import java.util.Optional;

public interface UserService {
    UserExtra validateAndGetUserExtra(String username);

    Optional<UserExtra> getUserExtra(String username);

    UserExtra saveUserExtra(UserExtra userExtra);
}
