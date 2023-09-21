package de.thm.holdem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class AvatarServiceImpl implements AvatarService {
    private final WebClient webClient;

    @Value("${avatar.api.url}")
    private String apiUrl;

    public AvatarServiceImpl() {
        this.webClient = WebClient.create(apiUrl);
    }

    public Mono<String> getRandomAvatar() {
        String seed = UUID.randomUUID().toString();
        String apiUrlWithSeed = String.format("%s?seed=%s", apiUrl, seed);

        return webClient
                .get()
                .uri(apiUrlWithSeed)
                .retrieve()
                .bodyToMono(String.class);
    }

    public String getRandomAvatarUrl() {
        String seed = UUID.randomUUID().toString();
        apiUrl = String.format("%s?seed=%s", apiUrl, seed);
        return apiUrl;
    }
}
