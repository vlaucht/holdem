package de.thm.holdem.controller;

import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.dto.PokerGameStateDto;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.service.PokerGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static de.thm.holdem.config.SwaggerConfig.BEARER_KEY_SECURITY_SCHEME;

@RestController
@RequestMapping("/api/poker")
@RequiredArgsConstructor
public class PokerGameController {

    private final PokerGameService pokerGameService;

    @Value("${api.base-url}")
    private String baseUrl;

    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping(value ="/join/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PokerGameStateDto> join(@PathVariable String gameId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        String playerId = jwt.getClaim("sub");
        PokerGame game = pokerGameService.joinGame(gameId, playerId);
        return ResponseEntity.ok().body(PokerGameStateDto.from(game));

    }

    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @GetMapping(value = "/state/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PokerGameStateDto> get(@AuthenticationPrincipal Jwt jwt, @PathVariable String gameId) throws Exception {
        String playerId = jwt.getClaim("sub");
        PokerGame game = pokerGameService.getGame(gameId);
        if (pokerGameService.isPlayerInGame(game, playerId)) {
            return ResponseEntity.ok().body(PokerGameStateDto.from(game));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PokerGameStateDto> create(@RequestBody @Valid PokerGameCreateRequest request, @AuthenticationPrincipal Jwt jwt) throws Exception {
        String playerId = jwt.getClaim("sub");
        PokerGame game = pokerGameService.createGame(playerId, request);
        PokerGameStateDto dto = PokerGameStateDto.from(game);
        //TODO get correct URI
        URI uri = URI.create(String.format("%s/api/poker/test", baseUrl));
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping("/leave/{gameId}")
    public ResponseEntity<Void> leave(@PathVariable String gameId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        String playerId = jwt.getClaim("sub");
        pokerGameService.leaveGame(gameId, playerId);
        return ResponseEntity.noContent().build();
    }


}
