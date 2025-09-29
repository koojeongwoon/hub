package com.tinyquest.hub.promotion.service;

import com.tinyquest.hub.promotion.application.PromotionLifecycleService;
import com.tinyquest.hub.promotion.domain.PromotionStatus;
import com.tinyquest.hub.promotion.domain.PromotionType;
import com.tinyquest.hub.promotion.domain.entity.Promotion;
import com.tinyquest.hub.promotion.domain.entity.PromotionBenefit;
import com.tinyquest.hub.promotion.domain.entity.PromotionCoupon;
import com.tinyquest.hub.promotion.domain.repository.PromotionBenefitRepository;
import com.tinyquest.hub.promotion.domain.repository.PromotionCouponRepository;
import com.tinyquest.hub.promotion.domain.repository.PromotionRepository;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionCouponRepository promotionCouponRepository;
    private final PromotionBenefitRepository promotionBenefitRepository;
    private final PromotionLifecycleService promotionLifecycleService;

    public Promotion create(String name, PromotionType type, Long actorId) {
        Promotion promotion = Promotion.create(name, type, actorId);
        return promotionRepository.save(promotion);
    }

    public Promotion activate(Long promotionId,
                              ChannelPreference preference,
                              List<NotificationRecipient> recipients) {
        Promotion promotion = getPromotion(promotionId);
        if (PromotionStatus.DRAFT.equals(promotion.getStatus())) {
            promotion.activate();
            promotionLifecycleService.notifyPromotionActivated(promotion, preference, recipients);
        }
        return promotion;
    }

    public PromotionCoupon issueCoupon(Long promotionId,
                                       String code,
                                       Long ownerId,
                                       Instant expirationAt,
                                       ChannelPreference preference,
                                       List<NotificationRecipient> recipients) {
        Promotion promotion = getPromotion(promotionId);
        PromotionCoupon coupon = promotionCouponRepository.save(
                PromotionCoupon.create(promotion, code, ownerId, expirationAt)
        );
        promotionLifecycleService.notifyCouponIssued(promotion, coupon, preference, recipients);
        return coupon;
    }

    public PromotionBenefit applyBenefit(Long promotionId,
                                         BigDecimal amount,
                                         String currency,
                                         String orderId,
                                         String benefitType,
                                         ChannelPreference preference,
                                         List<NotificationRecipient> recipients) {
        Promotion promotion = getPromotion(promotionId);
        PromotionBenefit benefit = promotionBenefitRepository.save(
                PromotionBenefit.create(promotion, amount, currency, orderId, benefitType)
        );
        promotionLifecycleService.notifyBenefitApplied(promotion, benefit, preference, recipients);
        return benefit;
    }

    private Promotion getPromotion(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("프로모션을 찾을 수 없습니다."));
    }
}
