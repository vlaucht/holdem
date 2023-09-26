package de.thm.holdem.model.game.poker;

public enum PokerPlayerAction {
    FOLD("fold"),
    CALL("call"),
    RAISE("raise"),
    CHECK("check"),
    ALL_IN("allIn"),
    SMALL_BLIND("smallBlind"),
    BIG_BLIND("bigBlind"),
    ;

    private final String stringValue;

    PokerPlayerAction(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
