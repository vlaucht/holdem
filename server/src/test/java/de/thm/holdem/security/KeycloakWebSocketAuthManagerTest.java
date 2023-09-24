package de.thm.holdem.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class KeycloakWebSocketAuthManagerTest {


    @Mock
    private KeycloakTokenVerifier tokenVerifier;

    @InjectMocks
    private KeycloakWebSocketAuthManager authManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Should_AuthenticateValidToken() throws VerificationException {

        Jwt jwt = createMockedJwt();
        when(tokenVerifier.verifyToken(anyString())).thenReturn(createMockedAccessToken());

        Authentication authentication = authManager.authenticate(new JwsAuthenticationToken(jwt.toString()));

        verify(tokenVerifier, times(1)).verifyToken(anyString());
        verifyNoMoreInteractions(tokenVerifier);
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals("test::id",authentication.getPrincipal());
    }

    @Test
    void Should_ThrowException_If_TokenIsInvalid() throws VerificationException {
        Jwt jwt = createMockedJwt();
        when(tokenVerifier.verifyToken(anyString())).thenThrow(JwtValidationException.class);

        assertThrows(JwtValidationException.class, () -> authManager.authenticate(new JwsAuthenticationToken(jwt.toString())));
    }

    private AccessToken createMockedAccessToken() {
        AccessToken accessToken = new AccessToken();
        accessToken.setSubject("test::id");
        AccessToken.Access accessTokenAccess = new AccessToken.Access();
        accessTokenAccess.addRole("test::role");
        accessToken.setRealmAccess(accessTokenAccess);
        return accessToken;
    }

    private Jwt createMockedJwt() {
        return Jwt.withTokenValue("test::token")
                .header("alg", "RS256")
                .claim("sub", "test::id")
                .claim("issuer", "test::issuer")
                .build();
    }

}