package com.tinyquest.hub.notification.application;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import java.time.Instant;
import java.util.List;

public record MarketingNotificationPayload(
        String messageId,
        String campaignName,
        String title,
        String body,
        Instant scheduledAt,
        ChannelPreference channelPreference,
        List<NotificationRecipient> recipients
) {

    public MarketingNotificationPayload {
        recipients = recipients == null ? List.of() : List.copyOf(recipients);
    }

    public boolean hasRecipients() {
        return !recipients.isEmpty();
    }
}
