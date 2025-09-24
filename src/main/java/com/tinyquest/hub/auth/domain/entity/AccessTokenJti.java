package com.tinyquest.hub.auth.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "access_token_jti",
        indexes = {
                @Index(name = "idx_jti_user", columnList = "user_id"),
                @Index(name = "idx_jti_expires", columnList = "expires_at")
        }
)
@Getter
@NoArgsConstructor(access=lombok.AccessLevel.PROTECTED)
@ToString
public class AccessTokenJti {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "jti", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID jti;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "session_id", columnDefinition = "BINARY(16)")
    private UUID sessionId;

    @Column(name = "issued_at", nullable = false, columnDefinition = "DATETIME(3)")
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false, columnDefinition = "DATETIME(3)")
    private Instant expiresAt;

    @Column(name = "revoked_at", columnDefinition = "DATETIME(3)")
    private Instant revokedAt;

    @Column(name = "revoke_reason", length = 100)
    private String revokeReason;

    // --- 팩토리 ---
    public static AccessTokenJti issue(Long userId, UUID sessionId, Instant expiresAt) {
        AccessTokenJti at = new AccessTokenJti();
        at.userId = userId;
        at.sessionId = sessionId;
        at.issuedAt = Instant.now();
        at.expiresAt = expiresAt;
        return at;
    }

    // --- 행위 메서드 ---
    public void revoke(String reason) {
        this.revokedAt = Instant.now();
        this.revokeReason = reason;
    }
}
