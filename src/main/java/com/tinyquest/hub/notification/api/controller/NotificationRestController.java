package com.tinyquest.hub.notification.api.controller;

import com.tinyquest.hub.notification.api.document.NotificationRestControllerDoc;
import com.tinyquest.hub.notification.api.dto.response.NotificationLogResponse;
import com.tinyquest.hub.notification.domain.entity.NotificationLog;
import com.tinyquest.hub.notification.service.NotificationQueryService;
import com.tinyquest.hub.shared.constants.ResourceCodes;
import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.RequirePermission;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationRestController implements NotificationRestControllerDoc {

    private final NotificationQueryService notificationQueryService;

    @Override
    @GetMapping("/logs")
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_NOTIFICATION_LOG_VIEW, actions = PermissionAction.VIEW)
    public List<NotificationLogResponse> latest(@RequestParam(defaultValue = "20") int size) {
        int pageSize = Math.min(Math.max(size, 1), 100);
        List<NotificationLog> logs = notificationQueryService.findLatest(pageSize);
        return logs.stream()
                .map(NotificationLogResponse::from)
                .toList();
    }
}
