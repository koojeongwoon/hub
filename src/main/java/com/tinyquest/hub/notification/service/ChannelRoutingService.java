package com.tinyquest.hub.notification.service;

import com.tinyquest.hub.notification.domain.DeliveryCommand;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ChannelRoutingService {

    public List<DeliveryCommand> route(ChannelPreference preference,
                                       List<NotificationRecipient> recipients,
                                       Object payload) {
        List<String> channels = resolveChannels(preference);
        if (recipients == null || recipients.isEmpty() || channels.isEmpty()) {
            return Collections.emptyList();
        }
        return recipients.stream()
                .flatMap(recipient -> channels.stream()
                        .map(channel -> new DeliveryCommand(channel, recipient, payload)))
                .collect(Collectors.toList());
    }

    private List<String> resolveChannels(ChannelPreference preference) {
        if (preference == null) {
            return List.of("EMAIL");
        }
        List<String> channels = new ArrayList<>();
        if (preference.preferredChannel() != null && !preference.preferredChannel().isBlank()) {
            channels.add(preference.preferredChannel());
        }
        channels.addAll(preference.fallbackChannels());
        return channels.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(channel -> !channel.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
