package com.tinyquest.hub.notification.domain.repository;

import com.tinyquest.hub.notification.domain.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}
