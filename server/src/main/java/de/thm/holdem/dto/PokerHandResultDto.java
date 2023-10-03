package de.thm.holdem.dto;

import de.thm.holdem.model.player.PokerPlayer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PokerHandResultDto {

    private String handType;
    private List<CardDto> handCards;

    public static PokerHandResultDto from(PokerPlayer player, boolean isPrivate) {
        if (player.getHand().getHandResult() == null) {
            return null;
        }
        PokerHandResultDto dto = new PokerHandResultDto();
        dto.setHandType(player.getHand().getHandResult().getHandType().getPrettyName());
        List<CardDto> cardDtoList = new ArrayList<>();
        player.getHand().getHandResult().getHandCards().forEach(card -> cardDtoList.add(CardDto.from(card, isPrivate)));
        dto.setHandCards(cardDtoList);
        return dto;
    }

}
