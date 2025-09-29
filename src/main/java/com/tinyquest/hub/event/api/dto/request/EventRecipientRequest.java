package com.tinyquest.hub.event.api.dto.request;

import com.tinyquest.hub.shared.port.notification.NotificationRecipient;

public record EventRecipientRequest(
        Long userId,
        String email,
        String phoneNumber
) {

    public NotificationRecipient toRecipient() {
        return new NotificationRecipient(userId, email, phoneNumber, null);
    }
}
