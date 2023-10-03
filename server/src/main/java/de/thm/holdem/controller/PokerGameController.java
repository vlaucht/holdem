package de.thm.holdem.controller;

import de.thm.holdem.dto.GameActionRequest;
import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.dto.PokerGameStateDto;
import de.thm.holdem.exception.ApiError;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.service.ConnectionRegistry;
import de.thm.holdem.service.PokerGameService;
import de.thm.holdem.service.WebsocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.thm.holdem.config.SwaggerConfig.BEARER_KEY_SECURITY_SCHEME;

/**
 * Rest controller for the poker game.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@RestController
@RequestMapping("/api/poker")
@RequiredArgsConstructor
public class PokerGameController {

    private final PokerGameService pokerGameService;
    private final ConnectionRegistry connectionRegistry;
    private final WebsocketService websocketService;
    private final Validator validator;

    @Value("${api.base-url}")
    private String baseUrl;

    /**
     * Route to join a poker game.
     *
     * @param gameId the id of the game to join
     * @param jwt the jwt of the authenticated user
     * @return the game state
     * @throws Exception if the game does not exist
     */
    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping(value ="/join/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PokerGameStateDto> join(@PathVariable String gameId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        String playerId = jwt.getClaim("sub");
        PokerGame game = pokerGameService.joinGame(gameId, playerId);
        return ResponseEntity.ok().body(PokerGameStateDto.from(game));
    }

    /**
     * Route to get the state of a poker game.
     *
     * @param jwt the jwt of the authenticated user.
     * @param gameId the id of the game.
     * @return the game state.
     * @throws Exception if the game does not exist or the user is not in the game and has no permission to see the game.
     */
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

    /**
     * Route to create a new poker game.
     *
     * @param request the request body.
     * @param jwt the jwt of the authenticated user.
     * @return the game state.
     * @throws Exception if the game could not be created.
     */
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

    /**
     * Route to leave a poker game.
     *
     * @param gameId the id of the game.
     * @param jwt the jwt of the authenticated user.
     * @return 204 if the user left the game.
     * @throws Exception if the game does not exist.
     */
    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping("/leave/{gameId}")
    public ResponseEntity<Void> leave(@PathVariable String gameId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        String playerId = jwt.getClaim("sub");
        pokerGameService.leaveGame(gameId, playerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Route to start a poker game.
     *
     * @param gameId the id of the game.
     * @param jwt the jwt of the authenticated user.
     * @return 204 if the game was started.
     * @throws Exception if the game does not exist or the user is not allowed to start the game.
     */
    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping("/start/{gameId}")
    public ResponseEntity<Void> start(@PathVariable String gameId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        String playerId = jwt.getClaim("sub");
        pokerGameService.startGame(gameId, playerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Route to perform an action in a poker game.
     *
     * @param request the request body.
     * @param accessor the websocket connection.
     */
    @MessageMapping("/poker-action")
    public void pokerAction(@Payload GameActionRequest request, SimpMessageHeaderAccessor accessor) {
        Set<ConstraintViolation<GameActionRequest>> violations = validator.validate(request);
        String sessionId = accessor.getSessionId();
        String playerId = connectionRegistry.getUserIdBySessionId(sessionId);
        if (!violations.isEmpty()) {
            ApiError apiError = getApiError(violations);
            websocketService.sendPrivateToSession(sessionId, "errors", apiError);
            return;
        }
        pokerGameService.performAction(request, playerId);
    }

    /**
     * Creates an {@link ApiError} from a set of {@link ConstraintViolation}.
     *
     * <p>
     *     Used to format validation errors.
     * </p>
     *
     * @param violations the violations.
     * @return the api error.
     */
    private ApiError getApiError(Set<ConstraintViolation<GameActionRequest>> violations) {
        Map<String, String> map = new HashMap<>();
        for (ConstraintViolation<GameActionRequest> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            map.put(propertyPath, message);
        }
        return new ApiError(
                Timestamp.from(Instant.now()),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                map.values().toString());
    }

}
