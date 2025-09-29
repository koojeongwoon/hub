package com.tinyquest.hub.notification.api.document;

import com.tinyquest.hub.notification.api.dto.request.NotificationCampaignLaunchRequest;
import com.tinyquest.hub.notification.api.dto.response.NotificationCampaignResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "5-3. Notification Campaign API", description = "마케팅 대상자 알림 발송")
public interface NotificationCampaignRestControllerDoc {

    @Operation(
            summary = "마케팅 알림 발송",
            description = "지정된 대상자에게 마케팅 알림을 즉시 발송합니다.",
            requestBody = @RequestBody(required = true, description = "마케팅 발송 요청", content = @Content(schema = @Schema(implementation = NotificationCampaignLaunchRequest.class))),
            responses = @ApiResponse(responseCode = "202", description = "발송 접수", content = @Content(schema = @Schema(implementation = NotificationCampaignResponse.class)))
    )
    NotificationCampaignResponse launch(@Valid NotificationCampaignLaunchRequest request);
}
