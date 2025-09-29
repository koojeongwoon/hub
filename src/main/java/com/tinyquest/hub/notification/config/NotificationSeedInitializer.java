package com.tinyquest.hub.notification.config;

import com.tinyquest.hub.notification.domain.entity.NotificationLog;
import com.tinyquest.hub.notification.domain.repository.NotificationLogRepository;
import java.util.List;
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
public class NotificationSeedInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(NotificationSeedInitializer.class);

    private final NotificationLogRepository notificationLogRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (notificationLogRepository.count() > 0) {
            return;
        }
        NotificationLog success = NotificationLog.success("seed-message-1", "EMAIL", "seed@example.com", "EventNotificationPayload");
        NotificationLog failure = NotificationLog.failure("seed-message-2", "SMS", "01000000000", "PromotionNotificationPayload", "Gateway timeout");
        notificationLogRepository.saveAll(List.of(success, failure));
        log.info("Seeded demo notification logs for local/dev profiles");
    }
}
