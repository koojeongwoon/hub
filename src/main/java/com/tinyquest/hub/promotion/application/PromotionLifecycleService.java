package com.tinyquest.hub.promotion.application;

import com.tinyquest.hub.promotion.domain.entity.Promotion;
import com.tinyquest.hub.promotion.domain.entity.PromotionBenefit;
import com.tinyquest.hub.promotion.domain.entity.PromotionCoupon;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPayload;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPublisherPort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionLifecycleService {

    private final PromotionNotificationPublisherPort notificationPublisherPort;
    private final PromotionNotificationPayloadFactory payloadFactory;

    public void notifyPromotionActivated(Promotion promotion,
                                         ChannelPreference preference,
                                         List<NotificationRecipient> recipients) {
        publish(payloadFactory.promotionActivated(promotion, preference, recipients, generateTraceId()));
    }

    public void notifyPromotionDeactivated(Promotion promotion,
                                           ChannelPreference preference,
                                           List<NotificationRecipient> recipients) {
        publish(payloadFactory.promotionDeactivated(promotion, preference, recipients, generateTraceId()));
    }

    public void notifyCouponIssued(Promotion promotion,
                                    PromotionCoupon coupon,
                                    ChannelPreference preference,
                                    List<NotificationRecipient> recipients) {
        publish(payloadFactory.couponIssued(promotion, coupon, preference, recipients, generateTraceId()));
    }

    public void notifyBenefitApplied(Promotion promotion,
                                      PromotionBenefit benefit,
                                      ChannelPreference preference,
                                      List<NotificationRecipient> recipients) {
        publish(payloadFactory.benefitApplied(promotion, benefit, preference, recipients, generateTraceId()));
    }

    private void publish(PromotionNotificationPayload payload) {
        notificationPublisherPort.publish(payload);
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
