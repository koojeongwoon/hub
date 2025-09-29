package com.tinyquest.hub.promotion.domain.repository;

import com.tinyquest.hub.promotion.domain.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
}
