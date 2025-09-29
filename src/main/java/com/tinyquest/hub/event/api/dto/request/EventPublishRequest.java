package com.tinyquest.hub.event.api.dto.request;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record EventPublishRequest(
        @Valid
        EventChannelPreferenceRequest channelPreference,
        @Valid
        @NotEmpty(message = "수신자 목록은 비어 있을 수 없습니다.")
        List<EventRecipientRequest> recipients
) {

    public ChannelPreference toChannelPreference() {
        return channelPreference != null ? channelPreference.toChannelPreference() : null;
    }

    public List<NotificationRecipient> toRecipients() {
        return recipients.stream()
                .map(EventRecipientRequest::toRecipient)
                .toList();
    }
}
