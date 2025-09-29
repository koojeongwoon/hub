package com.tinyquest.hub.event.api.dto.request;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EventParticipationRequest(
        @NotNull(message = "참여자 ID는 필수입니다.")
        Long participantId,
        @Valid
        EventChannelPreferenceRequest channelPreference,
        @Valid
        EventRecipientRequest recipient
) {

    public ChannelPreference toChannelPreference() {
        return channelPreference != null ? channelPreference.toChannelPreference() : null;
    }

    public List<NotificationRecipient> toRecipients() {
        return recipient != null ? List.of(recipient.toRecipient()) : List.of();
    }
}
