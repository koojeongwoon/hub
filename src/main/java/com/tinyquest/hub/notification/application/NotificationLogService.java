package com.tinyquest.hub.notification.application;

import com.tinyquest.hub.notification.domain.DeliveryCommand;
import com.tinyquest.hub.notification.domain.entity.NotificationLog;
import com.tinyquest.hub.notification.domain.repository.NotificationLogRepository;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;

    public void recordSuccess(String messageId, DeliveryCommand command) {
        NotificationLog log = NotificationLog.success(
                messageId,
                command.channel(),
                recipientLabel(command.recipient()),
                command.payload().getClass().getSimpleName()
        );
        notificationLogRepository.save(log);
    }

    public void recordFailure(String messageId, DeliveryCommand command, String errorMessage) {
        NotificationLog log = NotificationLog.failure(
                messageId,
                command.channel(),
                recipientLabel(command.recipient()),
                command.payload().getClass().getSimpleName(),
                errorMessage
        );
        notificationLogRepository.save(log);
    }

    private String recipientLabel(NotificationRecipient recipient) {
        if (recipient == null) {
            return "UNKNOWN";
        }
        if (recipient.prefersEmail()) {
            return recipient.email();
        }
        if (recipient.prefersSms()) {
            return recipient.phoneNumber();
        }
        if (recipient.hasUserId()) {
            return "user:" + recipient.userId();
        }
        return "UNKNOWN";
    }
}
