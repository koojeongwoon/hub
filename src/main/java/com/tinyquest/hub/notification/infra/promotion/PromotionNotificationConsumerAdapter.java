package com.tinyquest.hub.notification.infra.promotion;

import com.tinyquest.hub.notification.service.NotificationDispatchService;
import com.tinyquest.hub.notification.service.support.NotificationAsyncRunner;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPayload;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionNotificationConsumerAdapter implements PromotionNotificationPublisherPort {

    private final NotificationDispatchService dispatchService;
    private final NotificationAsyncRunner notificationAsyncRunner;

    @Override
    public void publish(PromotionNotificationPayload payload) {
        notificationAsyncRunner.runAsync(() -> dispatchService.dispatch(payload));
    }
}
