package com.tinyquest.hub.promotion.api.dto.request;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import java.util.List;

public record PromotionChannelPreferenceRequest(
        String preferredChannel,
        List<String> fallbackChannels
) {

    public ChannelPreference toChannelPreference() {
        return new ChannelPreference(preferredChannel, fallbackChannels);
    }
}
