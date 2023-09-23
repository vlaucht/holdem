package de.thm.holdem.security;

import de.thm.holdem.security.keycloak.KeycloakTokenVerifier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("websoket")
@AllArgsConstructor
public class WebSocketAuthenticationManager implements AuthenticationManager
{
    private final KeycloakTokenVerifier tokenVerifier;


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
            log.debug(token.getName());
            log.debug("haha");
            token = new JwsAuthenticationToken(tokenString, token.getName(), authorities);
            token.setAuthenticated(true);
        }
        catch (AuthenticationException e)
        {
            log.debug("Exception authenticating the token {}:", tokenString, e);
            throw new BadCredentialsException("Invalid token");
        }
        return token;
    }
}
