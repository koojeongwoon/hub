package com.tinyquest.hub.notification.api.controller;

import com.tinyquest.hub.notification.api.document.NotificationCampaignRestControllerDoc;
import com.tinyquest.hub.notification.api.dto.request.NotificationCampaignLaunchRequest;
import com.tinyquest.hub.notification.api.dto.response.NotificationCampaignResponse;
import com.tinyquest.hub.notification.service.NotificationCampaignService;
import com.tinyquest.hub.shared.constants.ResourceCodes;
import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.RequirePermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications/campaigns")
public class NotificationCampaignRestController implements NotificationCampaignRestControllerDoc {

    private final NotificationCampaignService notificationCampaignService;

    @Override
    @PostMapping
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_NOTIFICATION_CAMPAIGN_SEND, actions = PermissionAction.EXECUTE)
    public NotificationCampaignResponse launch(@Valid @RequestBody NotificationCampaignLaunchRequest request) {
        Long campaignId = notificationCampaignService.launchCampaign(
                request.name(),
                request.title(),
                request.body(),
                request.toPreference(),
                request.toRecipients()
        );
        return NotificationCampaignResponse.of(campaignId);
    }
}
