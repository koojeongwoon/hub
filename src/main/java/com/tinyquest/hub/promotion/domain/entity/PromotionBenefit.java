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
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotion_benefit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal benefitAmount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(length = 80)
    private String orderId;

    @Column(nullable = false, length = 30)
    private String benefitType;

    @Column(nullable = false, columnDefinition = "DATETIME(3)")
    private Instant appliedAt;

    private PromotionBenefit(Promotion promotion, BigDecimal benefitAmount, String currency,
                             String orderId, String benefitType) {
        this.promotion = promotion;
        this.benefitAmount = benefitAmount;
        this.currency = currency;
        this.orderId = orderId;
        this.benefitType = benefitType;
    }

    public static PromotionBenefit create(Promotion promotion, BigDecimal benefitAmount, String currency,
                                          String orderId, String benefitType) {
        return new PromotionBenefit(promotion, benefitAmount, currency, orderId, benefitType);
    }

    @PrePersist
    void prePersist() {
        this.appliedAt = Instant.now();
    }
}
