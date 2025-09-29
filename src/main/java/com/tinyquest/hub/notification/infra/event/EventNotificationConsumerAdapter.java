package com.tinyquest.hub.notification.infra.event;

import com.tinyquest.hub.notification.service.NotificationDispatchService;
import com.tinyquest.hub.notification.service.support.NotificationAsyncRunner;
import com.tinyquest.hub.shared.port.event.EventNotificationPayload;
import com.tinyquest.hub.shared.port.event.EventNotificationPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventNotificationConsumerAdapter implements EventNotificationPublisherPort {

    private final NotificationDispatchService dispatchService;
    private final NotificationAsyncRunner notificationAsyncRunner;

    @Override
    public void publish(EventNotificationPayload payload) {
        notificationAsyncRunner.runAsync(() -> dispatchService.dispatch(payload));
    }
}
