package de.thm.holdem.service;

import reactor.core.publisher.Mono;

public interface AvatarService {
    String getRandomAvatarUrl();

    Mono<String> getRandomAvatar();
}
