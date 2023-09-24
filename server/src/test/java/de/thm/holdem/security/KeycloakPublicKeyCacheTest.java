package de.thm.holdem.security;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeycloakPublicKeyCacheTest {

    private KeycloakPublicKeyCache keycloakPublicKeyCache;

    @Mock
    private Cache<String, PublicKey> cache;

    private final String keyId = "test::keyId";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        keycloakPublicKeyCache = new KeycloakPublicKeyCache();
        keycloakPublicKeyCache.cache = cache;
    }

    @Test
    void Should_PutPublicKeyIntoCache() {
        PublicKey publicKey = mock(PublicKey.class);

        keycloakPublicKeyCache.putPublicKey(keyId, publicKey);

        verify(cache, times(1)).put(keyId, publicKey);
    }

    @Test
    void Should_GetPublicKeyFromCache() {
        PublicKey publicKey = mock(PublicKey.class);

        when(cache.getIfPresent(keyId)).thenReturn(publicKey);

        PublicKey cachedKey = keycloakPublicKeyCache.getPublicKey(keyId);

        assertSame(publicKey, cachedKey);
    }

    @Test
    void Should_ThrowException_If_PublicKeyNotInCache() {

        when(cache.getIfPresent(keyId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> keycloakPublicKeyCache.getPublicKey(keyId));
    }

    @Test
    void Should_InitializeTheCache() {
        KeycloakPublicKeyCache spyCache = spy(keycloakPublicKeyCache);
        doNothing().when(spyCache).cachePublicKeys();
        doNothing().when(spyCache).startKeyRotationScheduler();

        spyCache.init();

        verify(spyCache, times(1)).cachePublicKeys();
        verify(spyCache, times(1)).startKeyRotationScheduler();
    }



}