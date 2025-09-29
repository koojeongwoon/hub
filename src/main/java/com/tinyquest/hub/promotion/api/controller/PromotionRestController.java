package com.tinyquest.hub.promotion.api.controller;

import com.tinyquest.hub.promotion.api.document.PromotionRestControllerDoc;
import com.tinyquest.hub.promotion.api.dto.request.PromotionActivateRequest;
import com.tinyquest.hub.promotion.api.dto.request.PromotionBenefitRequest;
import com.tinyquest.hub.promotion.api.dto.request.PromotionCouponIssueRequest;
import com.tinyquest.hub.promotion.api.dto.request.PromotionCreateRequest;
import com.tinyquest.hub.promotion.api.dto.response.PromotionBenefitResponse;
import com.tinyquest.hub.promotion.api.dto.response.PromotionCouponResponse;
import com.tinyquest.hub.promotion.api.dto.response.PromotionResponse;
import com.tinyquest.hub.promotion.domain.entity.Promotion;
import com.tinyquest.hub.promotion.domain.entity.PromotionBenefit;
import com.tinyquest.hub.promotion.domain.entity.PromotionCoupon;
import com.tinyquest.hub.promotion.service.PromotionService;
import com.tinyquest.hub.shared.constants.ResourceCodes;
import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.RequirePermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotions")
public class PromotionRestController implements PromotionRestControllerDoc {

    private final PromotionService promotionService;

    @Override
    @PostMapping
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_PROMOTION_MANAGEMENT, actions = PermissionAction.CREATE)
    public ResponseEntity<PromotionResponse> create(@Valid @RequestBody PromotionCreateRequest request) {
        Promotion promotion = promotionService.create(request.name(), request.type(), request.actorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(PromotionResponse.from(promotion));
    }

    @Override
    @PostMapping("/{promotionId}/activate")
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_PROMOTION_MANAGEMENT, actions = PermissionAction.APPROVE)
    public PromotionResponse activate(@PathVariable Long promotionId,
                                      @Valid @RequestBody PromotionActivateRequest request) {
        Promotion promotion = promotionService.activate(
                promotionId,
                request.toChannelPreference(),
                request.toRecipients()
        );
        return PromotionResponse.from(promotion);
    }

    @Override
    @PostMapping("/{promotionId}/coupons")
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_PROMOTION_INCENTIVE, actions = PermissionAction.CREATE)
    public PromotionCouponResponse issueCoupon(@PathVariable Long promotionId,
                                               @Valid @RequestBody PromotionCouponIssueRequest request) {
        PromotionCoupon coupon = promotionService.issueCoupon(
                promotionId,
                request.code(),
                request.ownerId(),
                request.expirationAt(),
                request.toChannelPreference(),
                request.toRecipients()
        );
        return PromotionCouponResponse.from(coupon);
    }

    @Override
    @PostMapping("/{promotionId}/benefits")
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_PROMOTION_INCENTIVE, actions = PermissionAction.EXECUTE)
    public PromotionBenefitResponse applyBenefit(@PathVariable Long promotionId,
                                                 @Valid @RequestBody PromotionBenefitRequest request) {
        PromotionBenefit benefit = promotionService.applyBenefit(
                promotionId,
                request.amount(),
                request.currency(),
                request.orderId(),
                request.benefitType(),
                request.toChannelPreference(),
                request.toRecipients()
        );
        return PromotionBenefitResponse.from(benefit);
    }
}
