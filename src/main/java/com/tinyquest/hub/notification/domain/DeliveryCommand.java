package com.tinyquest.hub.notification.domain;

import com.tinyquest.hub.shared.port.notification.NotificationRecipient;

/**
 * 채널별 발송을 담당하는 하위 컴포넌트가 이해할 최소 단위의 명령.
 */
public record DeliveryCommand(
        String channel,
        NotificationRecipient recipient,
        Object payload
) {}
