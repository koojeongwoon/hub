package com.tinyquest.hub.event.application;

import com.tinyquest.hub.event.domain.EventStatus;
import com.tinyquest.hub.event.domain.entity.Event;
import com.tinyquest.hub.event.domain.entity.EventEntry;
import com.tinyquest.hub.event.domain.entity.EventWinner;
import com.tinyquest.hub.shared.port.event.EventNotificationPayload;
import com.tinyquest.hub.shared.port.event.EventNotificationPayload.EventContext;
import com.tinyquest.hub.shared.port.event.EventNotificationPayload.EventLifecycleTrigger;
import com.tinyquest.hub.shared.port.event.EventNotificationPayload.ParticipationContext;
import com.tinyquest.hub.shared.port.event.EventNotificationPayload.SelectionContext;
import com.tinyquest.hub.shared.port.event.EventNotificationPayload.StatusTransition;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationPayloadFactory {

    private static final String SCHEMA_VERSION = "1.0";
    private static final String SOURCE = "event-module";

    public EventNotificationPayload eventPublished(Event event,
                                                   ChannelPreference preference,
                                                   List<NotificationRecipient> recipients,
                                                   String traceId) {
        return buildPayload(
                EventLifecycleTrigger.EVENT_PUBLISHED,
                event,
                null,
                null,
                null,
                preference,
                recipients,
                traceId
        );
    }

    public EventNotificationPayload entrySubmitted(Event event,
                                                   EventEntry entry,
                                                   ChannelPreference preference,
                                                   List<NotificationRecipient> recipients,
                                                   String traceId) {
        ParticipationContext participationContext = new ParticipationContext(
                entry.getId(),
                entry.getParticipantId()
        );
        return buildPayload(
                EventLifecycleTrigger.ENTRY_SUBMITTED,
                event,
                participationContext,
                null,
                null,
                preference,
                recipients,
                traceId
        );
    }

    public EventNotificationPayload winnerSelected(Event event,
                                                   EventWinner winner,
                                                   ChannelPreference preference,
                                                   List<NotificationRecipient> recipients,
                                                   String traceId) {
        SelectionContext selectionContext = new SelectionContext(
                winner.getPrizeId(),
                winner.getDrawRound() != null ? winner.getDrawRound().longValue() : null
        );
        return buildPayload(
                EventLifecycleTrigger.WINNER_SELECTED,
                event,
                null,
                selectionContext,
                null,
                preference,
                recipients,
                traceId
        );
    }

    public EventNotificationPayload statusChanged(Event event,
                                                  EventStatus previousStatus,
                                                  ChannelPreference preference,
                                                  List<NotificationRecipient> recipients,
                                                  String traceId) {
        StatusTransition transition = new StatusTransition(
                previousStatus.name(),
                event.getStatus().name()
        );
        return buildPayload(
                EventLifecycleTrigger.EVENT_STATUS_CHANGED,
                event,
                null,
                null,
                transition,
                preference,
                recipients,
                traceId
        );
    }

    private EventNotificationPayload buildPayload(EventLifecycleTrigger trigger,
                                                  Event event,
                                                  ParticipationContext participationContext,
                                                  SelectionContext selectionContext,
                                                  StatusTransition transition,
                                                  ChannelPreference preference,
                                                  List<NotificationRecipient> recipients,
                                                  String traceId) {
        EventContext context = new EventContext(
                event.getId(),
                event.getType(),
                event.getPublishedAt() != null ? event.getPublishedAt() : Instant.now()
        );
        return new EventNotificationPayload(
                UUID.randomUUID().toString(),
                SCHEMA_VERSION,
                SOURCE,
                traceId,
                trigger,
                context,
                participationContext,
                selectionContext,
                transition,
                preference,
                recipients
        );
    }
}
