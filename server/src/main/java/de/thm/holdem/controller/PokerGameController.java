package de.thm.holdem.controller;

import de.thm.holdem.dto.ConnectRequest;
import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.dto.PokerGameStateDto;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.service.PokerGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;

import static de.thm.holdem.config.SwaggerConfig.BEARER_KEY_SECURITY_SCHEME;

@RestController
@RequestMapping("/api/poker")
@RequiredArgsConstructor
public class PokerGameController {

    private final PokerGameService pokerGameService;

    @Value("${api.base-url}")
    private String baseUrl;

    @PostMapping("/join")
    public ResponseEntity<PokerGameStateDto> join(@RequestBody ConnectRequest request) throws Exception {
       // return ResponseEntity.ok(pokerGameService.joinGame(request.gameId(), request.playerName()));
        return null;
    }

    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PokerGameStateDto> create(@RequestBody @Valid PokerGameCreateRequest request, Principal principal) throws Exception {
        PokerGame game = pokerGameService.createGame(principal.getName(), request);
        PokerGameStateDto dto = PokerGameStateDto.from(game);
        //TODO get correct URI
        URI uri = URI.create(String.format("%s/api/poker/test", baseUrl));
        return ResponseEntity.created(uri).body(dto);
    }

}
