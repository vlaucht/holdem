package de.thm.holdem.dto;

import de.thm.holdem.model.game.poker.PokerGame;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PokerGameStateDto {

    private String id;

    private String name;

    private String gameStatus;

    private List<CardDto> flopCards;

    private CardDto turnCard;

    private CardDto riverCard;

    private ClientOperation operation;

    public static PokerGameStateDto from(PokerGame game) {
        PokerGameStateDto dto = new PokerGameStateDto();
        dto.setId(game.getId());
        dto.setName(game.getName());
        dto.setGameStatus(game.getGameStatus().getPrettyName());

        // TODO implement game state order, so that you can do something like state > State.FLOP
        return dto;
    }
}
