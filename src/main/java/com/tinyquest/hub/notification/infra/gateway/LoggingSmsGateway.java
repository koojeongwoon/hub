package com.tinyquest.hub.notification.infra.gateway;

import com.tinyquest.hub.notification.domain.DeliveryCommand;
import com.tinyquest.hub.notification.domain.port.SmsGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingSmsGateway implements SmsGateway {

    private static final Logger log = LoggerFactory.getLogger(LoggingSmsGateway.class);

    @Override
    public void send(DeliveryCommand command) {
        log.info("[SMS] 발송 recipient={} payloadType={} messageId={}",
                command.recipient(),
                command.payload().getClass().getSimpleName(),
                extractMessageId(command));
    }

    private String extractMessageId(DeliveryCommand command) {
        try {
            return (String) command.payload().getClass().getMethod("messageId").invoke(command.payload());
        } catch (Exception ignored) {
            return "unknown";
        }
    }
}
