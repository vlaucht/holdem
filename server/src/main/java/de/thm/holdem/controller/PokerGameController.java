package de.thm.holdem.controller;

import de.thm.holdem.dto.GameActionRequest;
import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.dto.PokerGameStateDto;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.service.ConnectionRegistry;
import de.thm.holdem.service.PokerGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static de.thm.holdem.config.SwaggerConfig.BEARER_KEY_SECURITY_SCHEME;

@RestController
@RequestMapping("/api/poker")
@RequiredArgsConstructor
public class PokerGameController {

    private final PokerGameService pokerGameService;
    private final ConnectionRegistry connectionRegistry;

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
            return ResponseEntity.ok().body(pokerGameService.mergePrivateInfo(game, playerId));
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

    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping("/start/{gameId}")
    public ResponseEntity<Void> start(@PathVariable String gameId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        String playerId = jwt.getClaim("sub");
        pokerGameService.startGame(gameId, playerId);
        return ResponseEntity.noContent().build();
    }

    @MessageMapping("/poker-action")
    public void sendMessage(@Payload @Valid GameActionRequest request, SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String playerId = connectionRegistry.getUserIdBySessionId(sessionId);
        pokerGameService.performAction(request, playerId);
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(MethodArgumentNotValidException ex, SimpMessageHeaderAccessor headerAccessor) {
        // Extract the validation errors
        Map<String, String> map = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> map.put(fieldError.getField(), fieldError.getDefaultMessage()));
        System.out.println(map.values().toString());
        // TODO send error message
        // Send the error response back to the client
        /*SimpMessagingTemplate messagingTemplate = new SimpMessagingTemplate(clientInboundChannel, clientOutboundChannel);
        messagingTemplate.convertAndSendToUser(headerAccessor.getUser().getName(), "/queue/errors", errorResponse);*/
    }


}
