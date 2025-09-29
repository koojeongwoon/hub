package com.tinyquest.hub.shared.port.event;

/**
 * 이벤트 도메인에서 발생한 상태 변화를 알림 모듈에 전달하기 위한 포트.
 */
public interface EventNotificationPublisherPort {

    void publish(EventNotificationPayload payload);
}
