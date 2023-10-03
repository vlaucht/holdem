package de.thm.holdem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.game.poker.PokerPlayerAction;
import de.thm.holdem.model.player.PokerPlayer;
import lombok.Data;

import java.util.List;

@Data
public class PokerPlayerStateDto {

    private String name;

    private String avatar;

    private List<CardDto> cards;

    private int chips;

    private int bet;

    private int potShare;

    private String lastAction;

    @JsonProperty("isDealer")
    private boolean isDealer;

    @JsonProperty("isSmallBlind")
    private boolean isSmallBlind;

    @JsonProperty("isBigBlind")
    private boolean isBigBlind;

    @JsonProperty("isActor")
    private boolean isActor;

    private List<String> allowedActions;

    private boolean mustShowCards;

    public static PokerPlayerStateDto from(PokerPlayer player, PokerGame game, boolean isPrivate) {
        PokerPlayerStateDto dto = new PokerPlayerStateDto();
        dto.setName(player.getAlias());
        dto.setAvatar(player.getAvatar());
        dto.setChips(player.getChips().intValue());
        dto.setBet(player.getCurrentBet().intValue());
        dto.setLastAction(player.getLastAction() != null ? player.getLastAction().getStringValue() : null);
        dto.setDealer(game.getDealer() != null && game.getDealer().equals(player));
        dto.setSmallBlind(game.getSmallBlindPlayer() != null && game.getSmallBlindPlayer().equals(player));
        dto.setBigBlind(game.getBigBlindPlayer() != null && game.getBigBlindPlayer().equals(player));
        dto.setActor(game.getActor() != null && game.getActor().equals(player));
        dto.setPotShare(player.getPotShare().intValue());

        if (isPrivate) {
            dto.setAllowedActions(player.getAllowedActions().stream().map(PokerPlayerAction::getStringValue).toList());
            dto.setMustShowCards(player.isMustShowCards());
        }

        if (player.hasHoleCards()) {
            dto.setCards(List.of(CardDto.from(player.getHoleCards().get(0), isPrivate), CardDto.from(player.getHoleCards().get(1), isPrivate)));
        }

        return dto;
    }

}
