package com.tinyquest.hub.promotion.application;

import com.tinyquest.hub.promotion.domain.entity.Promotion;
import com.tinyquest.hub.promotion.domain.entity.PromotionBenefit;
import com.tinyquest.hub.promotion.domain.entity.PromotionCoupon;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPayload;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPayload.BenefitContext;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPayload.CouponContext;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPayload.PromotionContext;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPayload.PromotionLifecycleTrigger;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PromotionNotificationPayloadFactory {

    private static final String SCHEMA_VERSION = "1.0";
    private static final String SOURCE = "promotion-module";

    public PromotionNotificationPayload promotionActivated(Promotion promotion,
                                                           ChannelPreference preference,
                                                           List<NotificationRecipient> recipients,
                                                           String traceId) {
        return buildPayload(
                PromotionLifecycleTrigger.PROMOTION_ACTIVATED,
                promotion,
                null,
                null,
                preference,
                recipients,
                traceId
        );
    }

    public PromotionNotificationPayload promotionDeactivated(Promotion promotion,
                                                             ChannelPreference preference,
                                                             List<NotificationRecipient> recipients,
                                                             String traceId) {
        return buildPayload(
                PromotionLifecycleTrigger.PROMOTION_DEACTIVATED,
                promotion,
                null,
                null,
                preference,
                recipients,
                traceId
        );
    }

    public PromotionNotificationPayload couponIssued(Promotion promotion,
                                                      PromotionCoupon couponIssue,
                                                      ChannelPreference preference,
                                                      List<NotificationRecipient> recipients,
                                                      String traceId) {
        CouponContext couponContext = new CouponContext(
                couponIssue.getCode(),
                couponIssue.getExpirationAt(),
                couponIssue.getOwnerId()
        );
        return buildPayload(
                PromotionLifecycleTrigger.COUPON_ISSUED,
                promotion,
                couponContext,
                null,
                preference,
                recipients,
                traceId
        );
    }

    public PromotionNotificationPayload benefitApplied(Promotion promotion,
                                                        PromotionBenefit benefit,
                                                        ChannelPreference preference,
                                                        List<NotificationRecipient> recipients,
                                                        String traceId) {
        BenefitContext context = new BenefitContext(
                benefit.getBenefitAmount(),
                benefit.getCurrency(),
                benefit.getOrderId(),
                benefit.getBenefitType()
        );
        return buildPayload(
                PromotionLifecycleTrigger.BENEFIT_APPLIED,
                promotion,
                null,
                context,
                preference,
                recipients,
                traceId
        );
    }

    private PromotionNotificationPayload buildPayload(PromotionLifecycleTrigger trigger,
                                                       Promotion promotion,
                                                       CouponContext couponContext,
                                                       BenefitContext benefitContext,
                                                       ChannelPreference preference,
                                                       List<NotificationRecipient> recipients,
                                                       String traceId) {
        PromotionContext promotionContext = new PromotionContext(
                promotion.getId(),
                promotion.getType().name(),
                promotion.getActivatedAt() != null ? promotion.getActivatedAt() : Instant.now(),
                promotion.getActorId()
        );
        return new PromotionNotificationPayload(
                UUID.randomUUID().toString(),
                SCHEMA_VERSION,
                SOURCE,
                traceId,
                trigger,
                promotionContext,
                couponContext,
                benefitContext,
                preference,
                recipients
        );
    }
}
