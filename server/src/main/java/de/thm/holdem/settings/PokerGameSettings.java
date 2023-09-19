package de.thm.holdem.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@ConfigurationProperties("poker-game-settings")
public class PokerGameSettings {
    private final int maxPlayers;
    private final int timeToRaiseBlinds;
    private final float timePerPlayerMove;

    private final int totalTournamentTime;

    public PokerGameSettings(int maxPlayers, int timeToRaiseBlinds, float timePerPlayerMove, int totalTournamentTime) {
        this.maxPlayers = maxPlayers;
        this.timeToRaiseBlinds = timeToRaiseBlinds;
        this.timePerPlayerMove = timePerPlayerMove;
        this.totalTournamentTime = totalTournamentTime;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getTimeToRaiseBlinds() {
        return this.timeToRaiseBlinds;
    }

    public float getTimePerPlayerMove() {
        return this.timePerPlayerMove;
    }

    public int getTotalTournamentTime() {
        return this.totalTournamentTime;
    }
}
