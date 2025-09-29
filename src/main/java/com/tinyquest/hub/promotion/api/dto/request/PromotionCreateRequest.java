package com.tinyquest.hub.promotion.api.dto.request;

import com.tinyquest.hub.promotion.domain.PromotionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PromotionCreateRequest(
        @NotBlank(message = "프로모션 이름은 필수입니다.")
        String name,
        @NotNull(message = "프로모션 유형은 필수입니다.")
        PromotionType type,
        @NotNull(message = "주체 ID는 필수입니다.")
        Long actorId
) {}
