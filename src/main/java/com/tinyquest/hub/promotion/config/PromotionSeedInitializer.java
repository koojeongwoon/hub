package com.tinyquest.hub.promotion.config;

import com.tinyquest.hub.promotion.domain.entity.Promotion;
import com.tinyquest.hub.promotion.domain.entity.PromotionBenefit;
import com.tinyquest.hub.promotion.domain.entity.PromotionCoupon;
import com.tinyquest.hub.promotion.domain.repository.PromotionBenefitRepository;
import com.tinyquest.hub.promotion.domain.repository.PromotionCouponRepository;
import com.tinyquest.hub.promotion.domain.repository.PromotionRepository;
import com.tinyquest.hub.promotion.domain.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class PromotionSeedInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PromotionSeedInitializer.class);

    private final PromotionRepository promotionRepository;
    private final PromotionCouponRepository promotionCouponRepository;
    private final PromotionBenefitRepository promotionBenefitRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (promotionRepository.count() > 0) {
            return;
        }

        Promotion welcome = Promotion.create("웰컴 쿠폰", PromotionType.COUPON, 2001L);
        promotionRepository.save(welcome);
        welcome.activate();
        promotionCouponRepository.save(
                PromotionCoupon.create(welcome, "WELCOME-9000", 1001L, Instant.now().plusSeconds(86400 * 14L))
        );

        Promotion doublePoint = Promotion.create("더블 포인트", PromotionType.REWARD, 2002L);
        promotionRepository.save(doublePoint);
        doublePoint.activate();
        doublePoint.deactivate();
        promotionBenefitRepository.save(
                PromotionBenefit.create(doublePoint, BigDecimal.valueOf(3000), "KRW", "ORDER-2024001", "POINT")
        );

        log.info("Seeded demo promotions for local/dev profiles: {}", promotionRepository.count());
    }
}
