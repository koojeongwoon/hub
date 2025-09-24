package com.tinyquest.hub.auth.infra.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyquest.hub.auth.config.JwtProperties;
import com.tinyquest.hub.auth.domain.entity.AccessTokenJti;
import com.tinyquest.hub.auth.domain.repository.AccessTokenJtiRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtVerifier {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_SESSION_ID = "sid";
    private static final String CLAIM_ROTATION = "rot";
    private final JwtProperties properties;
    private final JwtKeyStore keyStore;
    private final AccessTokenJtiRepository accessTokenJtiRepository;
    private final Clock clock;

    public AccessTokenClaims verifyAccessToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JwtVerificationException("Token is empty");
        }

        String kid = resolveKeyId(token);
        JwtKeyStore.JwtSigningKey signingKey = keyStore.findByKid(kid)
                .orElse(keyStore.getActiveKey());

        try {
            long skewSeconds = properties.getAllowedClockSkew() != null
                    ? properties.getAllowedClockSkew().getSeconds()
                    : 0L;
            int allowedSkew = (int) Math.min(Integer.MAX_VALUE, Math.max(0L, skewSeconds));

            var parserBuilder = Jwts.parser()
                    .requireIssuer(properties.getIssuer())
                    .clock(() -> Date.from(clock.instant()))
                    .clockSkewSeconds(allowedSkew)
                    .verifyWith((SecretKey) signingKey.verificationKey());

            var jws = parserBuilder.build().parseSignedClaims(token);
            Claims claims = jws.getPayload();

            UUID jti = Optional.ofNullable(claims.getId())
                    .map(UUID::fromString)
                    .orElseThrow(() -> new JwtVerificationException("Missing JTI"));

            Instant now = clock.instant();
            accessTokenJtiRepository.findById(jti).ifPresent(entity -> validateNotRevoked(entity, now));

            Number uidNumber = claims.get(CLAIM_USER_ID, Number.class);
            if (uidNumber == null) {
                throw new JwtVerificationException("Missing uid claim");
            }

            Long userId = uidNumber.longValue();
            String subject = claims.getSubject();
            Instant expiresAt = claims.getExpiration().toInstant();
            Instant issuedAt = claims.getIssuedAt() != null ? claims.getIssuedAt().toInstant() : null;

            UUID sessionId = Optional.ofNullable(claims.get("sid", String.class))
                    .filter(StringUtils::hasText)
                    .map(UUID::fromString)
                    .orElse(null);

            return new AccessTokenClaims(token, jti, userId, subject, issuedAt, expiresAt, sessionId, signingKey.kid(), claims);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new JwtVerificationException("Invalid access token", ex);
        }
    }

    public RefreshTokenClaims verifyRefreshToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JwtVerificationException("Token is empty");
        }

        String kid = resolveKeyId(token);
        JwtKeyStore.JwtSigningKey signingKey = keyStore.findByKid(kid)
                .orElse(keyStore.getActiveKey());

        try {
            long skewSeconds = properties.getAllowedClockSkew() != null
                    ? properties.getAllowedClockSkew().getSeconds()
                    : 0L;
            int allowedSkew = (int) Math.min(Integer.MAX_VALUE, Math.max(0L, skewSeconds));

            var parserBuilder = Jwts.parser()
                    .requireIssuer(properties.getIssuer())
                    .clock(() -> Date.from(clock.instant()))
                    .clockSkewSeconds(allowedSkew)
                    .verifyWith((SecretKey) signingKey.verificationKey());

            var jws = parserBuilder.build().parseSignedClaims(token);
            Claims claims = jws.getPayload();

            UUID jti = Optional.ofNullable(claims.getId())
                    .map(UUID::fromString)
                    .orElseThrow(() -> new JwtVerificationException("Missing JTI"));

            Number uidNumber = claims.get(CLAIM_USER_ID, Number.class);
            if (uidNumber == null) {
                throw new JwtVerificationException("Missing uid claim");
            }

            Long userId = uidNumber.longValue();
            String subject = claims.getSubject();
            Instant expiresAt = claims.getExpiration().toInstant();
            Instant issuedAt = claims.getIssuedAt() != null ? claims.getIssuedAt().toInstant() : null;

            UUID sessionId = Optional.ofNullable(claims.get(CLAIM_SESSION_ID, String.class))
                    .filter(StringUtils::hasText)
                    .map(UUID::fromString)
                    .orElse(null);

            Integer rotation = claims.get(CLAIM_ROTATION, Integer.class);

            return new RefreshTokenClaims(token, jti, userId, subject, issuedAt, expiresAt, sessionId, signingKey.kid(), rotation != null ? rotation : 0, claims);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new JwtVerificationException("Invalid refresh token", ex);
        }
    }

    private void validateNotRevoked(AccessTokenJti entity, Instant now) {
        if (entity.getRevokedAt() != null) {
            throw new JwtVerificationException("Token has been revoked");
        }
        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(now)) {
            throw new JwtVerificationException("Token has expired");
        }
    }

    private String resolveKeyId(String token) {
        int dotIndex = token.indexOf('.');
        if (dotIndex <= 0) {
            return null;
        }
        String headerSegment = token.substring(0, dotIndex);
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(headerSegment);
            Map<?, ?> header = OBJECT_MAPPER.readValue(decoded, Map.class);
            Object kid = header.get("kid");
            return kid != null ? kid.toString() : null;
        } catch (IllegalArgumentException e) {
            throw new JwtVerificationException("Invalid JWT header encoding", e);
        } catch (Exception e) {
            throw new JwtVerificationException("Unable to parse JWT header", e);
        }
    }

    public record AccessTokenClaims(
            String token,
            UUID jti,
            Long userId,
            String subject,
            Instant issuedAt,
            Instant expiresAt,
            UUID sessionId,
            String kid,
            Claims claims
    ) { }

    public record RefreshTokenClaims(
            String token,
            UUID jti,
            Long userId,
            String subject,
            Instant issuedAt,
            Instant expiresAt,
            UUID sessionId,
            String kid,
            int rotation,
            Claims claims
    ) { }
    public static class JwtVerificationException extends RuntimeException {
        public JwtVerificationException(String message) {
            super(message);
        }

        public JwtVerificationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
