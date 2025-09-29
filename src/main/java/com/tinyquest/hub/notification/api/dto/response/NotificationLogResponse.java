package com.tinyquest.hub.notification.api.dto.response;

import com.tinyquest.hub.notification.domain.NotificationStatus;
import com.tinyquest.hub.notification.domain.entity.NotificationLog;
import java.time.Instant;

public record NotificationLogResponse(
        Long id,
        String messageId,
        String channel,
        String recipient,
        String payloadType,
        NotificationStatus status,
        String errorMessage,
        Instant createdAt
) {

    public static NotificationLogResponse from(NotificationLog log) {
        return new NotificationLogResponse(
                log.getId(),
                log.getMessageId(),
                log.getChannel(),
                log.getRecipient(),
                log.getPayloadType(),
                log.getStatus(),
                log.getErrorMessage(),
                log.getCreatedAt()
        );
    }
}
