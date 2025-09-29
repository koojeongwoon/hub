package com.tinyquest.hub.notification.domain.entity;

import com.tinyquest.hub.notification.domain.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String messageId;

    @Column(nullable = false, length = 30)
    private String channel;

    @Column(nullable = false, length = 120)
    private String recipient;

    @Column(nullable = false, length = 120)
    private String payloadType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status;

    @Column(length = 250)
    private String errorMessage;

    @Column(nullable = false, columnDefinition = "DATETIME(3)")
    private Instant createdAt;

    private NotificationLog(String messageId,
                             String channel,
                             String recipient,
                             String payloadType,
                             NotificationStatus status,
                             String errorMessage) {
        this.messageId = messageId;
        this.channel = channel;
        this.recipient = recipient;
        this.payloadType = payloadType;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public static NotificationLog success(String messageId, String channel, String recipient, String payloadType) {
        return new NotificationLog(messageId, channel, recipient, payloadType, NotificationStatus.SENT, null);
    }

    public static NotificationLog failure(String messageId, String channel, String recipient,
                                          String payloadType, String errorMessage) {
        return new NotificationLog(messageId, channel, recipient, payloadType, NotificationStatus.FAILED, errorMessage);
    }

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }
}
