package com.tinyquest.hub.auth.infra.jwt;

import com.tinyquest.hub.auth.config.JwtProperties;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
public class JwtKeyStore {

    private final Map<String, JwtSigningKey> keysById;
    private final JwtSigningKey activeKey;

    public JwtKeyStore(
            JwtProperties properties,
            @Value("${jwt.secret:YourSuperSecretKeyForDevelopmentEnvironmentWhichIsLongEnough}") String fallbackSecret
    ) {
        Map<String, JwtSigningKey> tempKeys = new LinkedHashMap<>();
        JwtSigningKey candidateActive = null;

        for (JwtProperties.Key keyProp : properties.getKeys()) {
            JwtSigningKey signingKey = toSigningKey(keyProp);
            tempKeys.put(signingKey.kid(), signingKey);
            if (keyProp.isActive()) {
                candidateActive = signingKey;
            }
        }

        if (candidateActive == null) {
            candidateActive = tempKeys.values().stream().findFirst().orElse(null);
        }

        if (candidateActive == null) {
            JwtSigningKey fallback = JwtSigningKey.fromSecret("legacy", fallbackSecret);
            tempKeys.put(fallback.kid(), fallback);
            candidateActive = fallback;
        }

        this.keysById = Map.copyOf(tempKeys);
        this.activeKey = candidateActive;
    }

    private JwtSigningKey toSigningKey(JwtProperties.Key keyProp) {
        String kid = requireNonNull(keyProp.getKid(), "kid must not be null");

        if (StringUtils.hasText(keyProp.getSecret())) {
            return JwtSigningKey.fromSecret(kid, keyProp.getSecret());
        }

        if (StringUtils.hasText(keyProp.getPrivateKey())) {
            throw new IllegalArgumentException("Asymmetric keys are not supported yet for kid=" + kid);
        }

        throw new IllegalArgumentException("No key material configured for kid=" + kid);
    }

    public JwtSigningKey getActiveKey() {
        return activeKey;
    }

    public Optional<JwtSigningKey> findByKid(String kid) {
        if (!StringUtils.hasText(kid)) {
            return Optional.empty();
        }
        return Optional.ofNullable(keysById.get(kid));
    }

    public record JwtSigningKey(String kid, Key signingKey, Key verificationKey) {

        static JwtSigningKey fromSecret(String kid, String secret) {
            byte[] keyBytes = decodeSecret(secret);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);
            return new JwtSigningKey(kid, key, key);
        }

        private static byte[] decodeSecret(String secret) {
            if (!StringUtils.hasText(secret)) {
                throw new IllegalArgumentException("JWT secret must not be empty");
            }
            try {
                return Decoders.BASE64.decode(secret);
            } catch (IllegalArgumentException e) {
                // not base64, fall back to raw bytes
                return secret.getBytes(StandardCharsets.UTF_8);
            }
        }
    }
}
