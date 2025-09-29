package com.tinyquest.hub.notification.domain.repository;

import com.tinyquest.hub.notification.domain.entity.NotificationCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCampaignRepository extends JpaRepository<NotificationCampaign, Long> {
}
