package com.tinyquest.hub.promotion.api.dto.response;

import com.tinyquest.hub.promotion.domain.entity.PromotionCoupon;
import java.time.Instant;

public record PromotionCouponResponse(
        Long id,
        Long promotionId,
        String code,
        Long ownerId,
        Instant expirationAt,
        Instant issuedAt
) {

    public static PromotionCouponResponse from(PromotionCoupon coupon) {
        return new PromotionCouponResponse(
                coupon.getId(),
                coupon.getPromotion().getId(),
                coupon.getCode(),
                coupon.getOwnerId(),
                coupon.getExpirationAt(),
                coupon.getIssuedAt()
        );
    }
}
