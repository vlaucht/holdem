package de.thm.holdem.controller;

import de.thm.holdem.dto.PokerGameLobbyDto;
import de.thm.holdem.service.GameLobbyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.thm.holdem.config.SwaggerConfig.BEARER_KEY_SECURITY_SCHEME;

@RestController
@RequestMapping("/api/lobby")
@RequiredArgsConstructor
public class GameLobbyController {

    private final GameLobbyService gameLobbyService;

    @SendTo("/topic/lobby")
    public List<PokerGameLobbyDto> broadcast(@Payload List<PokerGameLobbyDto> games) {
        return games;
    }

    /**
     * Returns all poker games.
     *
     * @return list of all {@link de.thm.holdem.model.game.poker.PokerGame}
     */
    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PokerGameLobbyDto>> getAll() {
        return ResponseEntity.ok().body(gameLobbyService.getPokerGames());
    }
}
