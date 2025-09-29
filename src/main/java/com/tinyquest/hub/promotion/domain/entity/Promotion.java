package com.tinyquest.hub.promotion.domain.entity;

import com.tinyquest.hub.promotion.domain.PromotionStatus;
import com.tinyquest.hub.promotion.domain.PromotionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PromotionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromotionStatus status;

    @Column(columnDefinition = "DATETIME(3)")
    private Instant activatedAt;

    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME(3)")
    private Instant createdAt;

    @Column(nullable = false, columnDefinition = "DATETIME(3)")
    private Instant updatedAt;

    private Long actorId;

    private Promotion(String name, PromotionType type, Long actorId) {
        this.name = name;
        this.type = type;
        this.actorId = actorId;
        this.status = PromotionStatus.DRAFT;
    }

    public static Promotion create(String name, PromotionType type, Long actorId) {
        return new Promotion(name, type, actorId);
    }

    public boolean isDraft() {
        return PromotionStatus.DRAFT.equals(status);
    }

    public void activate() {
        if (!isDraft()) {
            throw new IllegalStateException("프로모션은 DRAFT 상태에서만 활성화할 수 있습니다.");
        }
        this.status = PromotionStatus.ACTIVE;
        this.activatedAt = Instant.now();
    }

    public void deactivate() {
        if (PromotionStatus.ACTIVE.equals(status)) {
            this.status = PromotionStatus.INACTIVE;
        }
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
