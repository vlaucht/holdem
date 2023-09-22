package de.thm.holdem.dto;

import de.thm.holdem.model.game.poker.PokerGame;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PokerGameLobbyDto {

    private String gameId;
    private String name;
    private int playerCount;
    private int maxPlayerCount;
    private String tableType;
    private int buyIn;
    private String gameStatus;
    private ClientOperation operation;

    public static PokerGameLobbyDto from(PokerGame game, ClientOperation operation) {
        return PokerGameLobbyDto.builder()
                .gameId(game.getId())
                .name(game.getName())
                .playerCount(game.getPlayerList().size())
                .maxPlayerCount(game.getMaxPlayerCount())
                .tableType(game.getTableType().getPrettyName())
                .buyIn(game.getBuyIn().intValue())
                .gameStatus(game.getGameStatus().getPrettyName())
                .operation(operation)
                .build();
    }

    public static PokerGameLobbyDto from(PokerGame game) {
        return from(game, ClientOperation.NONE);
    }

}
