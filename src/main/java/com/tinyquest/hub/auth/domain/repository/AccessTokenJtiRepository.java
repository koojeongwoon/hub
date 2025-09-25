package com.tinyquest.hub.auth.domain.repository;

import com.tinyquest.hub.auth.domain.entity.AccessTokenJti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccessTokenJtiRepository extends JpaRepository<AccessTokenJti, UUID> {

    List<AccessTokenJti> findAllByUserIdAndRevokedAtIsNullAndExpiresAtAfter(Long userId, Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AccessTokenJti a set a.revokedAt = :revokedAt, a.revokeReason = :reason " +
            "where a.jti = :jti and a.userId = :userId and a.revokedAt is null")
    void revokeByJti(@Param("jti") UUID jti,
                    @Param("userId") Long userId,
                    @Param("revokedAt") Instant revokedAt,
                    @Param("reason") String reason);

    Optional<AccessTokenJti> findByJtiAndUserId(UUID jti, Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AccessTokenJti a set a.revokedAt = :revokedAt, a.revokeReason = :reason " +
            "where a.sessionId = :sessionId and a.revokedAt is null")
    void revokeAllBySessionId(@Param("sessionId") UUID sessionId,
                             @Param("revokedAt") Instant revokedAt,
                             @Param("reason") String reason);
}
