package com.tinyquest.hub.notification.infra.channel;

import com.tinyquest.hub.notification.domain.DeliveryCommand;
import com.tinyquest.hub.notification.domain.port.NotificationChannelAdapter;
import com.tinyquest.hub.notification.domain.port.SmsGateway;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(110)
public class SmsNotificationChannelAdapter implements NotificationChannelAdapter {

    private static final String CHANNEL = "SMS";

    private final SmsGateway smsGateway;

    public SmsNotificationChannelAdapter(SmsGateway smsGateway) {
        this.smsGateway = smsGateway;
    }

    @Override
    public boolean supports(String channel) {
        return CHANNEL.equalsIgnoreCase(channel);
    }

    @Override
    public void send(DeliveryCommand command) {
        smsGateway.send(command);
    }
}
