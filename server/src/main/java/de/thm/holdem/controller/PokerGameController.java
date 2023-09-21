package de.thm.holdem.controller;

import de.thm.holdem.dto.ConnectRequest;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.service.PokerGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/poker")
@RequiredArgsConstructor
public class PokerGameController {

    private final PokerGameService pokerGameService;

    @PostMapping("/join")
    public ResponseEntity<PokerGame> join(@RequestBody ConnectRequest request) throws Exception {
        // TODO convert to dto
       // return ResponseEntity.ok(pokerGameService.joinGame(request.gameId(), request.playerName()));
        return null;
    }
}
