package com.tinyquest.hub.promotion.api.dto.response;

import com.tinyquest.hub.promotion.domain.entity.PromotionBenefit;
import java.math.BigDecimal;
import java.time.Instant;

public record PromotionBenefitResponse(
        Long id,
        Long promotionId,
        BigDecimal amount,
        String currency,
        String orderId,
        String benefitType,
        Instant appliedAt
) {

    public static PromotionBenefitResponse from(PromotionBenefit benefit) {
        return new PromotionBenefitResponse(
                benefit.getId(),
                benefit.getPromotion().getId(),
                benefit.getBenefitAmount(),
                benefit.getCurrency(),
                benefit.getOrderId(),
                benefit.getBenefitType(),
                benefit.getAppliedAt()
        );
    }
}
