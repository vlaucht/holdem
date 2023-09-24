package de.thm.holdem.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jws.JWSHeader;
import org.keycloak.representations.AccessToken;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeycloakTokenVerifierTest {

    private KeycloakTokenVerifier keycloakTokenVerifier;

    @Mock
    private KeycloakPublicKeyCache keycloakPublicKeyCache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        keycloakTokenVerifier = new KeycloakTokenVerifier(keycloakPublicKeyCache);
    }

    @Test
    void testVerifyToken() throws VerificationException {
        String tokenString = "test::tokenString";
        String keyId = "test::keyId";

        KeycloakTokenVerifier keycloakTokenVerifierSpy = spy(this.keycloakTokenVerifier);

        // Mock the PublicKey
        PublicKey publicKey = mock(PublicKey.class);
        when(keycloakPublicKeyCache.getPublicKey(keyId)).thenReturn(publicKey);

        // Mock TokenVerifier and AccessToken
        TokenVerifier<AccessToken> verifier = mock(TokenVerifier.class);
        AccessToken accessToken = mock(AccessToken.class);
        when(verifier.getHeader()).thenReturn(mock(JWSHeader.class));
        when(verifier.getHeader().getKeyId()).thenReturn(keyId);
        when(verifier.getToken()).thenReturn(accessToken);

        when(keycloakTokenVerifierSpy.getVerifier(tokenString)).thenReturn(verifier);

        doReturn(verifier).when(verifier).publicKey(publicKey);
        doReturn(verifier).when(verifier).verify();

        // Invoke the verifyToken method
        AccessToken result = keycloakTokenVerifierSpy.verifyToken(tokenString);

        // Verify that the getPublicKey method was called with the expected keyId
        verify(keycloakPublicKeyCache, times(1)).getPublicKey(keyId);

        // Verify that the publicKey and verify methods were called on TokenVerifier
        verify(verifier, times(1)).publicKey(publicKey);
        verify(verifier, times(1)).verify();

        // Verify that the method returned the expected AccessToken
        assertSame(accessToken, result);
    }
}