package com.tinyquest.hub.notification.domain.port;

import com.tinyquest.hub.notification.domain.DeliveryCommand;

public interface NotificationDlqWriter {

    void write(String messageId, DeliveryCommand command, String reason, Throwable cause);
}
