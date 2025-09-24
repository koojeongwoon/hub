package com.tinyquest.hub.auth.domain.repository;

import com.tinyquest.hub.auth.domain.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {

    List<AuthSession> findByUserIdAndRevokedAtIsNullOrderByCreatedAtDesc(Long userId);

    Optional<AuthSession> findByIdAndUserId(UUID id, Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AuthSession s set s.revokedAt = :revokedAt, s.revokeReason = :reason " +
            "where s.id = :sessionId and s.userId = :userId and s.revokedAt is null")
    int revokeSession(@Param("sessionId") UUID sessionId,
                      @Param("userId") Long userId,
                      @Param("revokedAt") Instant revokedAt,
                      @Param("reason") String reason);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AuthSession s set s.revokedAt = :revokedAt, s.revokeReason = :reason " +
            "where s.userId = :userId and s.revokedAt is null")
    int revokeAllActiveSessions(@Param("userId") Long userId,
                                @Param("revokedAt") Instant revokedAt,
                                @Param("reason") String reason);
}
