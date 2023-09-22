package de.thm.holdem.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CORS properties from application.yml
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "web.cors")
@Data
public class CorsProperties {
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
}