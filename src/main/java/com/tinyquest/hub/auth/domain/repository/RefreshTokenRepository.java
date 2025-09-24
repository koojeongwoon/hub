package com.tinyquest.hub.auth.domain.repository;

import com.tinyquest.hub.auth.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(byte[] tokenHash);

    Optional<RefreshToken> findTopBySession_IdOrderByRotationIndexDesc(UUID sessionId);

    Optional<RefreshToken> findTopBySession_IdAndRevokedAtIsNullOrderByRotationIndexDesc(UUID sessionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshToken r set r.revokedAt = :revokedAt " +
            "where r.session.id = :sessionId and r.revokedAt is null")
    int revokeAllBySessionId(@Param("sessionId") UUID sessionId,
                             @Param("revokedAt") Instant revokedAt);
}
