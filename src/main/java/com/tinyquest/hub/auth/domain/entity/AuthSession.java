package com.tinyquest.hub.auth.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "auth_session",
        indexes = {
                @Index(name = "idx_auth_session_user", columnList = "user_id"),
                @Index(name = "idx_auth_session_created", columnList = "created_at"),
                @Index(name = "idx_auth_session_revoked", columnList = "revoked_at"),
                @Index(name = "idx_auth_session_client", columnList = "client_id")
        }
)
@Getter
@NoArgsConstructor(access=lombok.AccessLevel.PROTECTED)
@ToString(exclude = "refreshTokens")
public class AuthSession {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "client_id", nullable = false, length = 40)
    private String clientId;

    @Column(name = "device_name", length = 255)
    private String deviceName;

    @Column(name = "device_fingerprint", columnDefinition = "VARBINARY(32)")
    private byte[] deviceFingerprint;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(3)")
    private Instant createdAt;

    @Column(name = "last_seen_at", columnDefinition = "DATETIME(3)")
    private Instant lastSeenAt;

    @Column(name = "revoked_at", columnDefinition = "DATETIME(3)")
    private Instant revokedAt;

    @Column(name = "revoke_reason", length = 100)
    private String revokeReason;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = false)
    @OrderBy("rotationIndex DESC")
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    // ---- 생성/팩토리 메서드 (세터 대신)
    public static AuthSession create(Long userId, String clientId, String deviceName, byte[] deviceFingerprint, String ip) {
        AuthSession s = new AuthSession();
        s.userId = userId;
        s.clientId = clientId;
        s.deviceName = deviceName;
        s.deviceFingerprint = deviceFingerprint; // 필요 시 defensive copy
        s.ip = ip;
        s.createdAt = Instant.now();
        return s;
    }

    // ---- 행위 메서드 (세터 대신 의미 있는 메서드로만 상태 변경)
    public void touchLastSeen() { this.lastSeenAt = Instant.now(); }
    public void revoke(String reason) {
        this.revokedAt = Instant.now();
        this.revokeReason = reason;
    }

    public void addRefreshToken(RefreshToken token) {
        token.setSession(this);
        this.refreshTokens.add(token);
    }
}
