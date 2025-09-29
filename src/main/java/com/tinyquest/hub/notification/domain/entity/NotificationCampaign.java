package com.tinyquest.hub.notification.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_campaign")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String body;

    @Column(nullable = false, length = 30)
    private String preferredChannel;

    @Column(nullable = false)
    private Instant createdAt;

    @ElementCollection
    private List<String> recipientEmails = new ArrayList<>();

    @ElementCollection
    private List<String> recipientPhones = new ArrayList<>();

    private NotificationCampaign(String name, String title, String body, String preferredChannel,
                                 List<String> recipientEmails, List<String> recipientPhones) {
        this.name = name;
        this.title = title;
        this.body = body;
        this.preferredChannel = preferredChannel;
        this.createdAt = Instant.now();
        if (recipientEmails != null) {
            this.recipientEmails.addAll(recipientEmails);
        }
        if (recipientPhones != null) {
            this.recipientPhones.addAll(recipientPhones);
        }
    }

    public static NotificationCampaign create(String name, String title, String body, String preferredChannel,
                                               List<String> recipientEmails, List<String> recipientPhones) {
        return new NotificationCampaign(name, title, body, preferredChannel, recipientEmails, recipientPhones);
    }
}
