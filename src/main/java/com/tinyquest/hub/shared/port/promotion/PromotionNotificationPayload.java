package com.tinyquest.hub.shared.port.promotion;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PromotionNotificationPayload(
        String messageId,
        String schemaVersion,
        String source,
        String traceId,
        PromotionLifecycleTrigger trigger,
        PromotionContext promotion,
        CouponContext coupon,
        BenefitContext benefit,
        ChannelPreference channelPreference,
        List<NotificationRecipient> recipients
) {

    public PromotionNotificationPayload {
        recipients = recipients == null ? List.of() : List.copyOf(recipients);
    }

    public boolean hasRecipients() {
        return !recipients.isEmpty();
    }

    public enum PromotionLifecycleTrigger {
        PROMOTION_ACTIVATED,
        PROMOTION_DEACTIVATED,
        COUPON_ISSUED,
        BENEFIT_APPLIED
    }

    public record PromotionContext(
            Long promotionId,
            String promotionType,
            Instant occurredAt,
            Long actorId
    ) {}

    public record CouponContext(
            String couponCode,
            Instant expirationAt,
            Long ownerId
    ) {}

    public record BenefitContext(
            BigDecimal benefitAmount,
            String currency,
            String orderId,
            String benefitType
    ) {}
}
