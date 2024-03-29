package de.thm.holdem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.thm.holdem.model.card.Card;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardDto {

    private String suit;
    private String rank;
    private String color;
    @JsonProperty("isFaceUp")
    private boolean isFaceUp;

    public static CardDto from(Card card, boolean isFaceUp) {
        CardDto cardDto = new CardDto();
        if (!isFaceUp) {
            cardDto.isFaceUp = false;
        } else {
            cardDto.suit = card.suit().getSymbol();
            cardDto.rank = card.rank().getSymbol();
            cardDto.color = card.suit().getColor();
            cardDto.isFaceUp = true;
        }
        return cardDto;
    }

    public static CardDto hidden() {
        CardDto cardDto = new CardDto();
        cardDto.setFaceUp(false);
        return cardDto;
    }
}
