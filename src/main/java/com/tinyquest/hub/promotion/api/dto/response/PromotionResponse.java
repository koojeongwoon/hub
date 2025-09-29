package com.tinyquest.hub.promotion.api.dto.response;

import com.tinyquest.hub.promotion.domain.PromotionStatus;
import com.tinyquest.hub.promotion.domain.PromotionType;
import com.tinyquest.hub.promotion.domain.entity.Promotion;
import java.time.Instant;

public record PromotionResponse(
        Long id,
        String name,
        PromotionType type,
        PromotionStatus status,
        Instant activatedAt
) {

    public static PromotionResponse from(Promotion promotion) {
        return new PromotionResponse(
                promotion.getId(),
                promotion.getName(),
                promotion.getType(),
                promotion.getStatus(),
                promotion.getActivatedAt()
        );
    }
}
