package com.tinyquest.hub.notification.domain.port;

import com.tinyquest.hub.notification.domain.DeliveryCommand;

public interface EmailGateway {

    void send(DeliveryCommand command);
}
