package de.thm.holdem.dto;

import de.thm.holdem.model.game.poker.BettingRound;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.player.PokerPlayer;
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

    private List<PokerPlayerStateDto> players;


    public static PokerGameStateDto from(PokerGame game, ClientOperation operation) {
        PokerGameStateDto dto = new PokerGameStateDto();
        dto.setId(game.getId());
        dto.setName(game.getName());
        dto.setGameStatus(game.getGameStatus().getPrettyName());
        dto.setOperation(operation);
        dto.setPlayers(game.getPlayerList().stream().map(player ->
                PokerPlayerStateDto.from((PokerPlayer) player, game, false)).toList());

        if (game.getBettingRound().isAfter(BettingRound.NONE)) {
            dto.setTurnCard(game.getTurnCard() != null ? CardDto.from(game.getTurnCard(), true) : CardDto.hidden());
            dto.setRiverCard(game.getRiverCard() != null ? CardDto.from(game.getRiverCard(), true) : CardDto.hidden());
            if (game.getFlopCards() == null || game.getFlopCards().size() == 0) {
                dto.setFlopCards(List.of(CardDto.hidden(), CardDto.hidden(), CardDto.hidden()));
            } else {
                dto.setFlopCards(game.getFlopCards().stream().map(card -> CardDto.from(card, true)).toList());
            }
        }

        return dto;
    }

    public static PokerGameStateDto from(PokerGame game) {
        return from(game, ClientOperation.NONE);
    }
}
