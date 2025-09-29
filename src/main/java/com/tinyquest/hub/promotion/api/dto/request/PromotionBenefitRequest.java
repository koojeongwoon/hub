package com.tinyquest.hub.promotion.api.dto.request;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record PromotionBenefitRequest(
        @NotNull(message = "혜택 금액은 필수입니다.")
        BigDecimal amount,
        @NotBlank(message = "통화 정보는 필수입니다.")
        String currency,
        @NotBlank(message = "주문 ID는 필수입니다.")
        String orderId,
        @NotBlank(message = "혜택 유형은 필수입니다.")
        String benefitType,
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
