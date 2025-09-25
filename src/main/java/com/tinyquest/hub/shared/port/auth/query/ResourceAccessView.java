package com.tinyquest.hub.shared.port.auth.query;

import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.ResourceType;

import java.util.Set;

public record ResourceAccessView(
        Long resourceId,
        ResourceType type,
        String code,
        String displayName,
        Long parentId,
        Integer displayOrder,
        boolean accessible,
        Set<PermissionAction> allowedActions,
        Set<PermissionAction> deniedActions
) {
}
