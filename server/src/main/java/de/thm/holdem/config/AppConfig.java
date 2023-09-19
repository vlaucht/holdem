package de.thm.holdem.config;

import de.thm.holdem.settings.PokerGameSettings;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(PokerGameSettings.class)
public class AppConfig {
}
