package com.tinyquest.hub.event.application;

import com.tinyquest.hub.event.domain.EventStatus;
import com.tinyquest.hub.event.domain.entity.Event;
import com.tinyquest.hub.event.domain.entity.EventEntry;
import com.tinyquest.hub.event.domain.entity.EventWinner;
import com.tinyquest.hub.shared.port.event.EventNotificationPayload;
import com.tinyquest.hub.shared.port.event.EventNotificationPublisherPort;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventLifecycleService {

    private final EventNotificationPublisherPort notificationPublisherPort;
    private final EventNotificationPayloadFactory payloadFactory;

    public void notifyEventPublished(Event event,
                                     ChannelPreference preference,
                                     List<NotificationRecipient> recipients) {
        publish(payloadFactory.eventPublished(event, preference, recipients, generateTraceId()));
    }

    public void notifyEntrySubmitted(Event event,
                                     EventEntry entry,
                                     ChannelPreference preference,
                                     List<NotificationRecipient> recipients) {
        publish(payloadFactory.entrySubmitted(event, entry, preference, recipients, generateTraceId()));
    }

    public void notifyWinnerSelected(Event event,
                                     EventWinner winner,
                                     ChannelPreference preference,
                                     List<NotificationRecipient> recipients) {
        publish(payloadFactory.winnerSelected(event, winner, preference, recipients, generateTraceId()));
    }

    public void notifyStatusChanged(Event event,
                                    EventStatus previousStatus,
                                    ChannelPreference preference,
                                    List<NotificationRecipient> recipients) {
        publish(payloadFactory.statusChanged(event, previousStatus, preference, recipients, generateTraceId()));
    }

    private void publish(EventNotificationPayload payload) {
        notificationPublisherPort.publish(payload);
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
