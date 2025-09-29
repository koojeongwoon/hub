package com.tinyquest.hub.shared.port.notification;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 수신자 정보를 표준화하여 발행 도메인과 알림 도메인 간 계약을 유지한다.
 */
public record NotificationRecipient(
        Long userId,
        String email,
        String phoneNumber,
        Map<String, String> attributes
) {

    public NotificationRecipient {
        attributes = attributes == null ? Collections.emptyMap() : Map.copyOf(attributes);
    }

    public static NotificationRecipient of(Long userId, String email, String phoneNumber) {
        return new NotificationRecipient(userId, email, phoneNumber, Collections.emptyMap());
    }

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean prefersEmail() {
        return email != null && !email.isBlank();
    }

    public boolean prefersSms() {
        return phoneNumber != null && !phoneNumber.isBlank();
    }

    public Map<String, String> attributes() {
        return attributes;
    }

    public String attributeOrDefault(String key, String defaultValue) {
        return attributes.getOrDefault(Objects.requireNonNull(key, "key"), defaultValue);
    }
}
