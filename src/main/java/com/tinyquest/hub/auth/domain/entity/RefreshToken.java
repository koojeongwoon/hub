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
        name = "refresh_token",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_refresh_token_hash", columnNames = {"token_hash"})
        },
        indexes = {
                @Index(name = "idx_refresh_session", columnList = "session_id,expires_at"),
                @Index(name = "idx_refresh_expires", columnList = "expires_at")
        }
)
@Getter
@NoArgsConstructor(access=lombok.AccessLevel.PROTECTED)
@ToString(exclude = "session")
public class RefreshToken {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false, columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_refresh_session"))
    @JdbcTypeCode(SqlTypes.BINARY)
    private AuthSession session;

    @Column(name = "token_hash", nullable = false, columnDefinition = "VARBINARY(32)")
    private byte[] tokenHash;

    @Column(name = "rotation_index", nullable = false)
    private int rotationIndex;

    @Column(name = "scope", length = 200)
    private String scope;

    @Column(name = "issued_at", nullable = false, columnDefinition = "DATETIME(3)")
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false, columnDefinition = "DATETIME(3)")
    private Instant expiresAt;

    @Column(name = "consumed_at", columnDefinition = "DATETIME(3)")
    private Instant consumedAt;

    @Column(name = "revoked_at", columnDefinition = "DATETIME(3)")
    private Instant revokedAt;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "replaced_by", columnDefinition = "BINARY(16)")
    private UUID replacedBy;

    @Column(name = "presented_fingerprint", columnDefinition = "VARBINARY(32)")
    private byte[] presentedFingerprint;

    // ---- 생성/팩토리
    public static RefreshToken issue(AuthSession session, byte[] tokenHash, int rotationIndex,
                                     String scope, Instant issuedAt, Instant expiresAt) {
        RefreshToken rt = new RefreshToken();
        rt.session = session;
        rt.tokenHash = tokenHash;
        rt.rotationIndex = rotationIndex;
        rt.scope = scope;
        rt.issuedAt = issuedAt;
        rt.expiresAt = expiresAt;
        return rt;
    }

    // ---- 행위 메서드
    public void markConsumed(Instant when) { this.consumedAt = when; }
    public void revoke(Instant when) { this.revokedAt = when; }
    public void linkReplacedBy(UUID newId) { this.replacedBy = newId; }

    public void setPresentedFingerprint(byte[] fingerprint) { this.presentedFingerprint = fingerprint; }

    // ---- 세션 연결 (세션에서 addRefreshToken에서만 호출)
    void setSession(AuthSession session) { this.session = session; }
}
