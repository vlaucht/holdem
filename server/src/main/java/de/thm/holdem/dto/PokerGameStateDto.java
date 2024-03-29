package de.thm.holdem.dto;

import de.thm.holdem.model.game.poker.BettingRound;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.player.PokerPlayer;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class PokerGameStateDto {

    private String id;

    private String name;

    private String gameStatus;

    private List<CardDto> flopCards;

    private CardDto turnCard;

    private CardDto riverCard;

    private ClientOperation operation;

    private List<PokerPlayerStateDto> players;

    private String bettingRound;

    private int maxPlayers;

    private int bigBlind;

    private List<Integer> pots;

    private int currentBet;

    private List<PokerPlayerStateDto> showdownOrder;


    public static PokerGameStateDto from(PokerGame game, ClientOperation operation) {
        PokerGameStateDto dto = new PokerGameStateDto();
        dto.setId(game.getId());
        dto.setMaxPlayers(game.getMaxPlayerCount());
        dto.setName(game.getName());
        dto.setGameStatus(game.getGameStatus().getPrettyName());
        dto.setOperation(operation);
        dto.setCurrentBet(game.getCurrentBet() != null ? game.getCurrentBet().intValue() : 0);
        dto.setPlayers(game.getPlayerList().stream().map(player ->
                PokerPlayerStateDto.from((PokerPlayer) player, game, false)).toList());
        dto.setBettingRound(game.getBettingRound().toString());
        dto.setPots(game.getPots().stream().map(pot -> pot.getPotSize().intValue()).toList());

        if (game.getBettingRound().isAfter(BettingRound.NONE)) {
            dto.setBigBlind(game.getSmallBlindLevels().get(game.getCurrentBlindLevel()).multiply(BigInteger.TWO).intValue());
            dto.setTurnCard(game.getTurnCard() != null ? CardDto.from(game.getTurnCard(), true) : CardDto.hidden());
            dto.setRiverCard(game.getRiverCard() != null ? CardDto.from(game.getRiverCard(), true) : CardDto.hidden());
            if (game.getFlopCards() == null || game.getFlopCards().size() == 0) {
                dto.setFlopCards(List.of(CardDto.hidden(), CardDto.hidden(), CardDto.hidden()));
            } else {
                dto.setFlopCards(game.getFlopCards().stream().map(card -> CardDto.from(card, true)).toList());
            }
        }

        if (operation == ClientOperation.SHOWDOWN) {
            dto.setShowdownOrder(game.getShowdownOrder().stream().map(player ->
                    PokerPlayerStateDto.from(player, game, player.mustShowCards())).toList());
        }

        return dto;
    }

    public static PokerGameStateDto from(PokerGame game) {
        return from(game, ClientOperation.NONE);
    }
}
