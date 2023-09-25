package de.thm.holdem.dto;

import de.thm.holdem.model.game.poker.PokerGame;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PokerGameStateDto {

    String id;

    String name;

    public static PokerGameStateDto from(PokerGame game) {
        return PokerGameStateDto.builder()
                .id(game.getId())
                .name(game.getName())
                .build();
    }
}
