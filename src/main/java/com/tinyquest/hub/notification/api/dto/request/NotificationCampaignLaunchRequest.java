package com.tinyquest.hub.notification.api.dto.request;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record NotificationCampaignLaunchRequest(
        @NotBlank(message = "캠페인 이름은 필수입니다.")
        String name,
        @NotBlank(message = "제목은 필수입니다.")
        String title,
        @NotBlank(message = "본문은 필수입니다.")
        String body,
        @Valid
        NotificationChannelPreferenceRequest channelPreference,
        @Valid
        @NotEmpty(message = "수신자 목록은 비어 있을 수 없습니다.")
        List<NotificationRecipientRequest> recipients
) {

    public ChannelPreference toPreference() {
        return channelPreference != null ? channelPreference.toPreference() : null;
    }

    public List<NotificationRecipient> toRecipients() {
        return recipients.stream()
                .map(NotificationRecipientRequest::toRecipient)
                .toList();
    }
}
