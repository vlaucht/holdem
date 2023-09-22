package de.thm.holdem.dto;

import de.thm.holdem.model.game.poker.PokerGame;
import lombok.Data;

@Data
public class PokerGameStateDto {
    String gameId;

    PokerGameStateDto() {
        this.gameId = "testGameId";
    }
    public static PokerGameStateDto from(PokerGame game) {
        return new PokerGameStateDto();
    }
}
