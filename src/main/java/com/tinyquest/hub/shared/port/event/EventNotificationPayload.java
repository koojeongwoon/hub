package com.tinyquest.hub.shared.port.event;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import java.time.Instant;
import java.util.List;

public record EventNotificationPayload(
        String messageId,
        String schemaVersion,
        String source,
        String traceId,
        EventLifecycleTrigger trigger,
        EventContext event,
        ParticipationContext participation,
        SelectionContext selection,
        StatusTransition status,
        ChannelPreference channelPreference,
        List<NotificationRecipient> recipients
) {

    public EventNotificationPayload {
        recipients = recipients == null ? List.of() : List.copyOf(recipients);
    }

    public boolean hasRecipients() {
        return !recipients.isEmpty();
    }

    public enum EventLifecycleTrigger {
        EVENT_PUBLISHED,
        ENTRY_SUBMITTED,
        WINNER_SELECTED,
        EVENT_STATUS_CHANGED
    }

    public record EventContext(
            Long eventId,
            String eventType,
            Instant occurredAt
    ) {}

    public record ParticipationContext(
            Long entryId,
            Long participantId
    ) {}

    public record SelectionContext(
            Long prizeId,
            Long drawRoundId
    ) {}

    public record StatusTransition(
            String before,
            String after
    ) {}
}
