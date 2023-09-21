package de.thm.holdem.security.keycloak;

import de.thm.holdem.security.WebSecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class KeycloakInitializerRunner implements CommandLineRunner {

    private final Keycloak keycloakAdmin;

    @Override
    public void run(String... args) {
        log.info("Initializing '{}' realm in Keycloak ...", CARD_GAME_SERVICES_REALM_NAME);

        Optional<RealmRepresentation> representationOptional = keycloakAdmin.realms()
                .findAll()
                .stream()
                .filter(r -> r.getRealm().equals(CARD_GAME_SERVICES_REALM_NAME))
                .findAny();
        if (representationOptional.isPresent()) {
            log.info("Realm '{}' already created. Aborting...", CARD_GAME_SERVICES_REALM_NAME);
            return;
        }

        // Realm
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(CARD_GAME_SERVICES_REALM_NAME);
        realmRepresentation.setDisplayName("Card Games");
        realmRepresentation.setEnabled(true);
        realmRepresentation.setRegistrationAllowed(true);

        // Client
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(CARD_GAME_APP_CLIENT_ID);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setPublicClient(true);
        clientRepresentation.setRedirectUris(List.of(CARD_GAME_APP_REDIRECT_URL));
        clientRepresentation.setDefaultRoles(new String[]{WebSecurityConfig.CARDGAME_USER});
        realmRepresentation.setClients(List.of(clientRepresentation));

        // Create Realm
        keycloakAdmin.realms().create(realmRepresentation);

        log.info("'{}' initialization completed successfully!", CARD_GAME_SERVICES_REALM_NAME);
    }


    private static final String CARD_GAME_SERVICES_REALM_NAME = "cardgame-services";
    private static final String CARD_GAME_APP_CLIENT_ID = "hold-em-app";
    private static final String CARD_GAME_APP_REDIRECT_URL = "http://localhost:3000/*";

}