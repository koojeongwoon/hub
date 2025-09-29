package com.tinyquest.hub.notification.api.dto.request;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import java.util.List;

public record NotificationChannelPreferenceRequest(
        String preferredChannel,
        List<String> fallbackChannels
) {

    public ChannelPreference toPreference() {
        return new ChannelPreference(preferredChannel, fallbackChannels);
    }
}
