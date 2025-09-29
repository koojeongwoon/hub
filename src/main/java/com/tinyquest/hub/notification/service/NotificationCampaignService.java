package com.tinyquest.hub.notification.service;

import com.tinyquest.hub.notification.application.MarketingNotificationPayload;
import com.tinyquest.hub.notification.domain.entity.NotificationCampaign;
import com.tinyquest.hub.notification.domain.repository.NotificationCampaignRepository;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCampaignService {

    private final NotificationCampaignRepository notificationCampaignRepository;
    private final NotificationDispatchService notificationDispatchService;

    public Long launchCampaign(String name,
                               String title,
                               String body,
                               ChannelPreference preference,
                               List<NotificationRecipient> recipients) {
        NotificationCampaign campaign = notificationCampaignRepository.save(
                NotificationCampaign.create(
                        name,
                        title,
                        body,
                        preference != null ? preference.preferredChannel() : "EMAIL",
                        recipients.stream()
                                .map(NotificationRecipient::email)
                                .filter(email -> email != null && !email.isBlank())
                                .toList(),
                        recipients.stream()
                                .map(NotificationRecipient::phoneNumber)
                                .filter(phone -> phone != null && !phone.isBlank())
                                .toList()
                )
        );

        MarketingNotificationPayload payload = new MarketingNotificationPayload(
                UUID.randomUUID().toString(),
                campaign.getName(),
                title,
                body,
                Instant.now(),
                preference,
                recipients
        );
        notificationDispatchService.dispatch(payload);
        return campaign.getId();
    }
}
