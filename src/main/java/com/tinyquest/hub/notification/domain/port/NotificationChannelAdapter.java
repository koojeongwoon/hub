package com.tinyquest.hub.notification.domain.port;

import com.tinyquest.hub.notification.domain.DeliveryCommand;

public interface NotificationChannelAdapter {

    boolean supports(String channel);

    void send(DeliveryCommand command);
}
