package de.thm.holdem.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to cache the public keys of a keycloak realm.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Component
@Slf4j
public class KeycloakPublicKeyCache {

    /** The keycloak server url. */
    @Value("${keycloak.auth.server-url}")
    private String keycloakServerUrl;

    /** The keycloak realm. */
    @Value("${keycloak.auth.realm}")
    private String realm;

    /** The cache expiration time in minutes. */
    private static final long CACHE_EXPIRATION_MINUTES = 60;

    /** The cache for the public keys. */
    protected Cache<String, PublicKey> cache;

    /**
     * Constructor to set up the cache.
     */
    public KeycloakPublicKeyCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(CACHE_EXPIRATION_MINUTES, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Initialize the cache by fetching the public keys from the keycloak realm certs endpoint
     * and starting the key rotation scheduler.
     */
    public void init() {
        log.info("Initializing Keycloak public key cache ...");
        cachePublicKeys();
        startKeyRotationScheduler();
    }

    /**
     * Get the keycloak realm certs url.
     *
     * @return the keycloak realm certs url
     */
    private String getRealmCertsUrl() {
        return String.format("%s/realms/%s/protocol/openid-connect/certs", keycloakServerUrl, realm);
    }

    /**
     * Start the key rotation scheduler to update the public keys in the cache periodically.
     */
    void startKeyRotationScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Fetch and update the public keys in the cache periodically
        scheduler.scheduleAtFixedRate(this::cachePublicKeys, 0, CACHE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Get all public keys from the keycloak realm certs endpoint.
     *
     * @return the list of public keys
     * @throws IOException if the request fails
     */
    List<Map<String, Object>> getPublicKeysFromServer() throws IOException {
        ObjectMapper om = new ObjectMapper();
        Map<String, Object> certInfos = om.readValue(new URL(getRealmCertsUrl()).openStream(), Map.class);
        List<Map<String, Object>> keys = (List<Map<String, Object>>) certInfos.get("keys");
        return keys;
    }

    /**
     * Get all public keys and put them in the cache.
     */
    void cachePublicKeys() {
        try {
            List<Map<String, Object>> keys = getPublicKeysFromServer();

            for (Map<String, Object> key : keys) {
                String kid = (String) key.get("kid");
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                String modulusBase64 = (String) key.get("n");
                String exponentBase64 = (String) key.get("e");
                Base64.Decoder urlDecoder = Base64.getUrlDecoder();
                BigInteger modulus = new BigInteger(1, urlDecoder.decode(modulusBase64));
                BigInteger publicExponent = new BigInteger(1, urlDecoder.decode(exponentBase64));

                PublicKey publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));

                putPublicKey(kid, publicKey);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Put a public key in the cache.
     *
     * @param keyId the key id
     * @param publicKey the public key
     */
    void putPublicKey(String keyId, PublicKey publicKey) {
        cache.put(keyId, publicKey);
    }

    /**
     * Get a public key from the cache if it exists or throw an exception.
     *
     * @param keyId the key id
     * @return the public key
     */
    public PublicKey getPublicKey(String keyId) {
        PublicKey key = cache.getIfPresent(keyId);
        if (key == null) {
            throw new RuntimeException("No public key found for keyId: " + keyId);
        }
        return key;
    }

}
