package com.tinyquest.hub.shared.port.notification;

import java.util.Collections;
import java.util.List;

/**
 * 채널 선호도를 통해 알림 모듈이 우선 경로와 대체 경로를 선택하도록 돕는다.
 */
public record ChannelPreference(
        String preferredChannel,
        List<String> fallbackChannels
) {

    public ChannelPreference {
        fallbackChannels = fallbackChannels == null ? List.of() : List.copyOf(fallbackChannels);
    }

    public static ChannelPreference of(String preferredChannel, List<String> fallbackChannels) {
        return new ChannelPreference(preferredChannel, fallbackChannels);
    }

    public static ChannelPreference emailOnly() {
        return new ChannelPreference("EMAIL", Collections.emptyList());
    }

    public static ChannelPreference smsOnly() {
        return new ChannelPreference("SMS", Collections.emptyList());
    }
}
