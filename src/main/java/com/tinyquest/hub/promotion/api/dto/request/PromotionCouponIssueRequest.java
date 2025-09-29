package com.tinyquest.hub.promotion.api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public record PromotionCouponIssueRequest(
        @NotBlank(message = "쿠폰 코드는 필수입니다.")
        String code,
        @NotNull(message = "소유자 ID는 필수입니다.")
        Long ownerId,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant expirationAt,
        @Valid
        PromotionChannelPreferenceRequest channelPreference,
        @Valid
        List<PromotionRecipientRequest> recipients
) {

    public ChannelPreference toChannelPreference() {
        return channelPreference != null ? channelPreference.toChannelPreference() : null;
    }

    public List<NotificationRecipient> toRecipients() {
        return recipients != null ? recipients.stream()
                .map(PromotionRecipientRequest::toRecipient)
                .toList() : List.of();
    }
}
