package de.thm.holdem.dto;

import de.thm.holdem.model.game.poker.PokerGame;
import lombok.Builder;
import lombok.Data;

/**
 * Data transfer object for poker games in the lobby
 *
 * @see PokerGame
 *
 * @author Valentin Laucht
 * @version 1.0
 */
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

    /**
     * Creates a PokerGameLobbyDto from a PokerGame with a {@link ClientOperation}
     * @param game The PokerGame to create the DTO from
     * @param operation The operation to be performed on the game
     * @return The created DTO
     */
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

    /**
     * Creates a PokerGameLobbyDto from a PokerGame with no {@link ClientOperation
     *
     * @param game The PokerGame to create the DTO from
     * @return The created DTO
     */
    public static PokerGameLobbyDto from(PokerGame game) {
        return from(game, ClientOperation.NONE);
    }

}
