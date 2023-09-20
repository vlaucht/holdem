package de.thm.holdem.settings;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@ConfigurationPropertiesScan
@ConfigurationProperties("poker-game-settings")
@Component
@Getter
@Setter
public class PokerGameSettings {
    private int maxPlayers;
    private int timeToRaiseBlinds;
    private float timePerPlayerMove;

    private int totalTournamentTime;

}
