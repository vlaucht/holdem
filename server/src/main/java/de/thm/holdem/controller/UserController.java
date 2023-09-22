package de.thm.holdem.controller;

import de.thm.holdem.model.user.UserExtra;
import de.thm.holdem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
     * @param principal the authenticated user
     * @return the {@link UserExtra} information
     */
    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserExtra> getUserExtra(Principal principal) {
        return ResponseEntity.ok().body(userExtraService.getUserExtra(principal.getName()));
    }

    /**
     * Recharges the user's bankroll to the initial value.
     *
     * @param principal the authenticated user
     * @return the {@link UserExtra} information
     */
    @Operation(security = {@SecurityRequirement(name = BEARER_KEY_SECURITY_SCHEME)})
    @PostMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserExtra> recharge(Principal principal) {
        return ResponseEntity.ok().body(userExtraService.recharge(principal.getName()));
    }
}
