package de.thm.holdem.dto;

import java.util.List;

public class PokerPlayerStateDto {

    private String name;

    private String avatar;

    private List<CardDto> cards;

    private int chips;

    private int bet;

    private String lastAction;

    private boolean isDealer;
    private boolean isSmallBlind;

    private boolean isBigBlind;

    private List<String> allowedActions;

}
