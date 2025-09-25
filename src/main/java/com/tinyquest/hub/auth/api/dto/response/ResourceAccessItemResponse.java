package com.tinyquest.hub.auth.api.dto.response;

import com.tinyquest.hub.shared.permission.ResourceType;

import java.util.List;

public record ResourceAccessItemResponse(
        Long resourceId,
        ResourceType type,
        String code,
        String displayName,
        Long parentId,
        Integer displayOrder,
        boolean accessible,
        List<String> allowedActions,
        List<String> deniedActions
) {
}
