package com.tinyquest.hub.auth.domain.entity;

import com.tinyquest.hub.auth.constants.TokenEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "token_audit",
        indexes = {
                @Index(name = "idx_audit_user_created", columnList = "user_id,created_at"),
                @Index(name = "idx_audit_type_created", columnList = "event_type,created_at")
        }
)
@Getter
@NoArgsConstructor(access=lombok.AccessLevel.PROTECTED)
@ToString
public class TokenAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // BIGINT AUTO_INCREMENT
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "session_id", columnDefinition = "BINARY(16)")
    private UUID sessionId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "refresh_id", columnDefinition = "BINARY(16)")
    private UUID refreshId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private TokenEventType eventType;

    /**
     * MariaDB JSON → 하이버네이트에서는 String으로 직렬화/역직렬화 권장
     * (원하면 @JdbcTypeCode(SqlTypes.JSON) 사용 가능)
     */
    @Column(name = "detail", columnDefinition = "JSON")
    private String detail;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(3)")
    private Instant createdAt;

    // --- 팩토리 ---
    public static TokenAudit of(
            Long userId,
            UUID sessionId,
            UUID refreshId,
            TokenEventType type,
            String jsonDetail
    ) {
        TokenAudit ta = new TokenAudit();
        ta.userId = userId;
        ta.sessionId = sessionId;
        ta.refreshId = refreshId;
        ta.eventType = type;
        ta.detail = jsonDetail;
        ta.createdAt = Instant.now();
        return ta;
    }

    // --- 편의 ---
    public void appendDetail(String json) {
        // 간단 병합(서비스단에서 JSON 라이브러리로 병합 권장)
        if (this.detail == null || this.detail.isBlank()) {
            this.detail = json;
        } else {
            // 매우 단순한 문자열 합치기 예시(실서비스는 안전한 JSON 병합 사용)
            this.detail = this.detail.substring(0, this.detail.length() - 1)
                    + "," + json.substring(1);
        }
    }
}
