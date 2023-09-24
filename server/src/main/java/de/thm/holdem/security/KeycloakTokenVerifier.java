package de.thm.holdem.security;

import lombok.RequiredArgsConstructor;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.stereotype.Component;
import java.security.PublicKey;

/**
 * Class to verify a websocket keycloak JWT
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class KeycloakTokenVerifier {

    private final KeycloakPublicKeyCache keycloakPublicKeyCache;

    /**
     * Verifies a token against a keycloak instance
     *
     * @param tokenString the string representation of the jws token
     * @return a validated keycloak AccessToken
     * @throws VerificationException when the token is not valid
     */
    public AccessToken verifyToken(String tokenString) throws VerificationException {
        TokenVerifier<AccessToken> verifier = getVerifier(tokenString);
        PublicKey publicKey = keycloakPublicKeyCache.getPublicKey(verifier.getHeader().getKeyId());
        verifier.publicKey(publicKey);
        verifier.verify();
        return verifier.getToken();
    }

    /**
     * Creates a TokenVerifier for the given tokenString
     *
     * @param tokenString the string representation of the jws token
     * @return a TokenVerifier for the given tokenString
     */
    TokenVerifier<AccessToken> getVerifier(String tokenString) {
        return TokenVerifier.create(tokenString, AccessToken.class);
    }

}
