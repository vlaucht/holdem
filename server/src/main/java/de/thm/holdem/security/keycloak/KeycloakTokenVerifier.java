package de.thm.holdem.security.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class KeycloakTokenVerifier {


    public AccessToken verifyToken(String token) throws AuthenticationException {
        try {
            log.debug("Verifying token: {}", token);
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(token, AccessToken.class);
            AccessToken accessToken = verifier.getToken();
            verifier.verify();
            // TODO add additional checks
            return accessToken;
        } catch (Exception e) {
            throw new AuthenticationException("Failed to verify token.");
        }
    }

}
