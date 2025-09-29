package com.tinyquest.hub.notification.service;

import com.tinyquest.hub.notification.application.MarketingNotificationPayload;
import com.tinyquest.hub.notification.application.NotificationLogService;
import com.tinyquest.hub.notification.domain.DeliveryCommand;
import com.tinyquest.hub.notification.domain.port.NotificationChannelAdapter;
import com.tinyquest.hub.notification.domain.port.NotificationDlqWriter;
import com.tinyquest.hub.shared.port.event.EventNotificationPayload;
import com.tinyquest.hub.shared.port.promotion.PromotionNotificationPayload;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationDispatchService {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatchService.class);

    private final ChannelRoutingService channelRoutingService;
    private final List<NotificationChannelAdapter> channelAdapters;
    private final NotificationLogService notificationLogService;
    private final NotificationDlqWriter notificationDlqWriter;
    private final RetryTemplate notificationRetryTemplate;

    public void dispatch(MarketingNotificationPayload payload) {
        if (payload == null || !payload.hasRecipients()) {
            log.debug("마케팅 알림 수신: 수신자 없음 messageId={}", payload != null ? payload.messageId() : "null");
            return;
        }
        List<DeliveryCommand> commands = channelRoutingService.route(
                payload.channelPreference(),
                payload.recipients(),
                payload
        );
        publish(commands, payload.messageId());
    }

    public void dispatch(EventNotificationPayload payload) {
        if (payload == null || !payload.hasRecipients()) {
            log.debug("이벤트 알림 수신: 수신자 없음 messageId={}", payload != null ? payload.messageId() : "null");
            return;
        }
        List<DeliveryCommand> commands = channelRoutingService.route(
                payload.channelPreference(),
                payload.recipients(),
                payload
        );
        publish(commands, payload.messageId());
    }

    public void dispatch(PromotionNotificationPayload payload) {
        if (payload == null || !payload.hasRecipients()) {
            log.debug("프로모션 알림 수신: 수신자 없음 messageId={}", payload != null ? payload.messageId() : "null");
            return;
        }
        List<DeliveryCommand> commands = channelRoutingService.route(
                payload.channelPreference(),
                payload.recipients(),
                payload
        );
        publish(commands, payload.messageId());
    }

    private void publish(List<DeliveryCommand> commands, String messageId) {
        if (commands.isEmpty()) {
            log.debug("알림 라우팅 결과 없음 messageId={}", messageId);
            return;
        }
        AnnotationAwareOrderComparator.sort(channelAdapters);
        for (DeliveryCommand command : commands) {
            channelAdapters.stream()
                    .filter(adapter -> adapter.supports(command.channel()))
                    .findFirst()
                    .ifPresentOrElse(adapter -> send(command, adapter, messageId),
                            () -> handleUnsupportedChannel(messageId, command));
        }
    }

    private void send(DeliveryCommand command, NotificationChannelAdapter adapter, String messageId) {
        try {
            notificationRetryTemplate.execute(context -> {
                adapter.send(command);
                return null;
            });
            log.debug("알림 발송 성공 messageId={} channel={} recipient={}",
                    messageId,
                    command.channel(),
                    command.recipient());
            notificationLogService.recordSuccess(messageId, command);
        } catch (Exception ex) {
            log.error("알림 발송 실패 messageId={} channel={} recipient={} cause={}",
                    messageId,
                    command.channel(),
                    command.recipient(),
                    ex.getMessage(),
                    ex);
            notificationLogService.recordFailure(messageId, command, ex.getMessage());
            notificationDlqWriter.write(messageId, command, ex.getMessage(), ex);
        }
    }

    private void handleUnsupportedChannel(String messageId, DeliveryCommand command) {
        log.warn("지원하지 않는 채널로 알림 요청 messageId={} channel={} recipient={}",
                messageId,
                command.channel(),
                command.recipient());
        notificationLogService.recordFailure(messageId, command, "지원하지 않는 채널");
        notificationDlqWriter.write(messageId, command, "UNSUPPORTED_CHANNEL", null);
    }
}
