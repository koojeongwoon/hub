package com.tinyquest.hub.auth.infra.jwt;

import com.tinyquest.hub.auth.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static com.tinyquest.hub.shared.utils.Hashing.sha256;

@Component
@RequiredArgsConstructor
public class JwtIssuer {

    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_SESSION_ID = "sid";
    private static final String CLAIM_SCOPE = "scp";
    private final JwtProperties properties;
    private final JwtKeyStore keyStore;
    private final Clock clock;

    public AccessToken issueAccessToken(Long userId, String subject, UUID sessionId) {
        return issueAccessToken(userId, subject, sessionId, Collections.emptyMap());
    }

    public AccessToken issueAccessToken(Long userId, String subject, UUID sessionId, Map<String, Object> additionalClaims) {
        JwtKeyStore.JwtSigningKey signingKey = keyStore.getActiveKey();
        Instant now = clock.instant();
        Instant expiresAt = now.plus(properties.getAccessTokenTtl());
        UUID jti = UUID.randomUUID();

        JwtBuilder builder = Jwts.builder()
                .id(jti.toString())
                .issuer(properties.getIssuer())
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim(CLAIM_USER_ID, userId)
                .signWith(signingKey.signingKey());

        builder.header().type("JWT").keyId(signingKey.kid());

        if (sessionId != null) {
            builder.claim(CLAIM_SESSION_ID, sessionId.toString());
        }

        if (additionalClaims != null && !additionalClaims.isEmpty()) {
            additionalClaims.forEach(builder::claim);
        }

        String token = builder.compact();
        return new AccessToken(token, jti, now, expiresAt, signingKey.kid());
    }

    public RefreshToken issueRefreshToken(Long userId, String subject, UUID sessionId, int rotationIndex, String scope) {
        return issueRefreshToken(userId, subject, sessionId, rotationIndex, scope, Collections.emptyMap());
    }

    public RefreshToken issueRefreshToken(Long userId, String subject, UUID sessionId, int rotationIndex, String scope, Map<String, Object> additionalClaims) {
        JwtKeyStore.JwtSigningKey signingKey = keyStore.getActiveKey();
        Instant now = clock.instant();
        Instant expiresAt = now.plus(properties.getRefreshTokenTtl());
        UUID tokenId = UUID.randomUUID();

        JwtBuilder builder = Jwts.builder()
                .id(tokenId.toString())
                .issuer(properties.getIssuer())
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim(CLAIM_USER_ID, userId)
                .claim("rot", rotationIndex)
                .signWith(signingKey.signingKey());

        builder.header().type("JWT").keyId(signingKey.kid());

        if (sessionId != null) {
            builder.claim(CLAIM_SESSION_ID, sessionId.toString());
        }

        if (scope != null) {
            builder.claim(CLAIM_SCOPE, scope);
        }

        if (additionalClaims != null && !additionalClaims.isEmpty()) {
            additionalClaims.forEach(builder::claim);
        }

        String token = builder.compact();
        byte[] hash = sha256(token);
        return new RefreshToken(token, tokenId, now, expiresAt, hash, signingKey.kid(), sessionId, rotationIndex, scope);
    }

    public record AccessToken(String token, UUID jti, Instant issuedAt, Instant expiresAt, String kid) { }

    public record RefreshToken(
            String token,
            UUID id,
            Instant issuedAt,
            Instant expiresAt,
            byte[] tokenHash,
            String kid,
            UUID sessionId,
            int rotationIndex,
            String scope
    ) {
    }
}
