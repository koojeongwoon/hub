package com.tinyquest.hub.shared.port.auth.query;

import com.tinyquest.hub.shared.permission.ResourceType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record UserResourceAccessView(
        Long userId,
        Set<ResourceType> scopes,
        List<ResourceAccessView> resources
) {

    public Map<ResourceType, List<ResourceAccessView>> groupByType() {
        Map<ResourceType, List<ResourceAccessView>> grouped = new EnumMap<>(ResourceType.class);
        for (ResourceAccessView resource : resources) {
            grouped.computeIfAbsent(resource.type(), ignored -> new java.util.ArrayList<>()).add(resource);
        }
        return grouped;
    }

    public static UserResourceAccessView empty(Long userId, Set<ResourceType> scopes) {
        return new UserResourceAccessView(userId, scopes, List.of());
    }
}
