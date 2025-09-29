package com.tinyquest.hub.event.api.dto.request;

import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import java.util.List;

public record EventChannelPreferenceRequest(
        String preferredChannel,
        List<String> fallbackChannels
) {

    public ChannelPreference toChannelPreference() {
        return new ChannelPreference(preferredChannel, fallbackChannels);
    }
}
