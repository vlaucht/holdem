package de.thm.holdem.controller;

import de.thm.holdem.model.user.UserExtra;
import de.thm.holdem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static de.thm.holdem.config.SwaggerConfig.BEARER_KEY_SECURITY_SCHEME;

/**
 * Controller for user related requests.
 *
 * @see de.thm.holdem.model.user.UserExtra
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userExtraService;

    /**
     * Returns the user extra information for the given user.
     *
     * @param jwt the bearer token
     * @return the {@link UserExtra} information
     */
    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserExtra> getUserExtra(@AuthenticationPrincipal Jwt jwt) {
        String id = jwt.getClaim("sub");
        String username = jwt.getClaim("preferred_username");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        UserExtra userExtra = userExtraService.getUserExtra(id, username);
        return ResponseEntity.ok().headers(headers).body(userExtra);
    }

    /**
     * Recharges the user's bankroll to the initial value.
     *
     * @param jwt the bearer token
     * @return the {@link UserExtra} information
     */
    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserExtra> recharge(@AuthenticationPrincipal Jwt jwt) {
        String id = jwt.getClaim("sub");
        return ResponseEntity.ok().body(userExtraService.recharge(id));
    }
}
