package de.thm.holdem.controller;

import de.thm.holdem.exception.NotFoundException;
import de.thm.holdem.model.user.UserExtra;
import de.thm.holdem.service.ConnectionRegistry;
import de.thm.holdem.service.PokerGameService;
import de.thm.holdem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller for websocket events.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final ConnectionRegistry connectionRegistry;

    private final UserService userService;

    private final PokerGameService gameService;

    /**
     * Method to handle a websocket disconnect event.
     *
     * <p>
     *     If the user disconnects while playing a game, he will be removed from the game after 30 seconds
     *     if he does not reconnect.
     * </p>
     *
     * @param event the disconnect event
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String userId = connectionRegistry.disconnect(sessionId);

        UserExtra userExtra = userService.getUserExtra(userId);
        // if the user is not currently playing a game, everything is fine
        if (userExtra.getActiveGameId() == null) {
            log.debug("User {} disconnected and is not playing a game.", userExtra.getUsername());
            return;
        }
        log.debug("User {} disconnected and is playing a game.", userExtra.getUsername());
        // if the user is playing a game, give him 30 seconds to reconnect, otherwise remove him from the game
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if the user has reconnected
                if (!connectionRegistry.isConnected(userId)) {
                    log.debug("User {} did not reconnect.", userExtra.getUsername());
                    // User has not reconnected, perform actions
                    String gameId = userExtra.getActiveGameId();

                    // remove him from the game
                    try {
                        log.debug("Removing user {} from game {}", userExtra.getUsername(), gameId);
                        gameService.leaveGame(gameId, userId);
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                // Cancel the timer
                timer.cancel();
            }
        }, 30000); // 30 seconds delay before checking reconnection
    }



}
