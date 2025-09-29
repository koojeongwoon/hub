package com.tinyquest.hub.notification.infra.channel;

import com.tinyquest.hub.notification.domain.DeliveryCommand;
import com.tinyquest.hub.notification.domain.port.EmailGateway;
import com.tinyquest.hub.notification.domain.port.NotificationChannelAdapter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class EmailNotificationChannelAdapter implements NotificationChannelAdapter {

    private static final String CHANNEL = "EMAIL";

    private final EmailGateway emailGateway;

    public EmailNotificationChannelAdapter(EmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    @Override
    public boolean supports(String channel) {
        return CHANNEL.equalsIgnoreCase(channel);
    }

    @Override
    public void send(DeliveryCommand command) {
        emailGateway.send(command);
    }
}
