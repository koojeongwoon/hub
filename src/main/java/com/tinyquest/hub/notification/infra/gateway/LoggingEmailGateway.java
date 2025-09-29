package com.tinyquest.hub.notification.infra.gateway;

import com.tinyquest.hub.notification.domain.DeliveryCommand;
import com.tinyquest.hub.notification.domain.port.EmailGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingEmailGateway implements EmailGateway {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailGateway.class);

    @Override
    public void send(DeliveryCommand command) {
        log.info("[EMAIL] 발송 recipient={} payloadType={} messageId={}",
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
