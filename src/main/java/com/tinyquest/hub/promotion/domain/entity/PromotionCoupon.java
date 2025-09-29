package com.tinyquest.hub.promotion.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotion_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(nullable = false, length = 64, unique = true)
    private String code;

    @Column(nullable = false)
    private Long ownerId;

    @Column(columnDefinition = "DATETIME(3)")
    private Instant expirationAt;

    @Column(nullable = false, columnDefinition = "DATETIME(3)")
    private Instant issuedAt;

    private PromotionCoupon(Promotion promotion, String code, Long ownerId, Instant expirationAt) {
        this.promotion = promotion;
        this.code = code;
        this.ownerId = ownerId;
        this.expirationAt = expirationAt;
    }

    public static PromotionCoupon create(Promotion promotion, String code, Long ownerId, Instant expirationAt) {
        return new PromotionCoupon(promotion, code, ownerId, expirationAt);
    }

    @PrePersist
    void prePersist() {
        this.issuedAt = Instant.now();
    }
}
