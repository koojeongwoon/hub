package com.tinyquest.hub.shared.port.promotion;

/**
 * 프로모션 도메인에서 발생하는 혜택 이벤트를 알림 모듈에 전달하기 위한 포트.
 */
public interface PromotionNotificationPublisherPort {

    void publish(PromotionNotificationPayload payload);
}
