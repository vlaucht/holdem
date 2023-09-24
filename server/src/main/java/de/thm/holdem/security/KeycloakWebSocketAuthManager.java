package de.thm.holdem.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.VerificationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.keycloak.representations.AccessToken;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom AuthenticationManager to authenticate the websocket connection.
 *
 * <p>It uses the {@link KeycloakTokenVerifier} to verify the token;
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Component
@Slf4j
@Qualifier("websoket")
@RequiredArgsConstructor
public class KeycloakWebSocketAuthManager implements AuthenticationManager
{
    private final KeycloakTokenVerifier tokenVerifier;

    /**
     * Method to authenticate the websocket connection using the {@link KeycloakTokenVerifier}
     * and store the user id in the {@link JwsAuthenticationToken}
     *
     * @param authentication the authentication request object
     * @return the authenticated authentication object
     */
    @Override
    public Authentication authenticate(Authentication authentication)
    {
        JwsAuthenticationToken token = (JwsAuthenticationToken) authentication;
        String tokenString = (String) token.getCredentials();
        try
        {
            AccessToken accessToken = tokenVerifier.verifyToken(tokenString);
            List<GrantedAuthority> authorities = accessToken.getRealmAccess().getRoles().stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            String principal = accessToken.getSubject();
            token = new JwsAuthenticationToken(tokenString, principal, authorities);
            token.setAuthenticated(true);
        } catch (VerificationException e) {
            throw new RuntimeException(e);
        }
        return token;
    }
}
