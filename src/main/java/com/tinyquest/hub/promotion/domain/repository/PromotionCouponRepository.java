package com.tinyquest.hub.promotion.domain.repository;

import com.tinyquest.hub.promotion.domain.entity.Promotion;
import com.tinyquest.hub.promotion.domain.entity.PromotionCoupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionCouponRepository extends JpaRepository<PromotionCoupon, Long> {

    Optional<PromotionCoupon> findByPromotionAndCode(Promotion promotion, String code);
}
