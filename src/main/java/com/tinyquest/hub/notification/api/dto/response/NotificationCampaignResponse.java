package com.tinyquest.hub.notification.api.dto.response;

public record NotificationCampaignResponse(Long campaignId) {

    public static NotificationCampaignResponse of(Long id) {
        return new NotificationCampaignResponse(id);
    }
}
