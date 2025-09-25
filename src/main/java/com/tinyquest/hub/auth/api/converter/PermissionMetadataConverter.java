package com.tinyquest.hub.auth.api.converter;

import com.tinyquest.hub.auth.api.dto.response.ResourceAccessItemResponse;
import com.tinyquest.hub.auth.api.dto.response.UserResourceAccessResponse;
import com.tinyquest.hub.shared.permission.ResourceType;
import com.tinyquest.hub.shared.port.auth.query.ResourceAccessView;
import com.tinyquest.hub.shared.port.auth.query.UserResourceAccessView;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class PermissionMetadataConverter {

    public UserResourceAccessResponse convert(UserResourceAccessView view) {
        Map<ResourceType, List<ResourceAccessView>> grouped = view.groupByType();
        List<ResourceAccessItemResponse> menus = convert(grouped.get(ResourceType.MENU));
        List<ResourceAccessItemResponse> pages = convert(grouped.get(ResourceType.PAGE));
        List<ResourceAccessItemResponse> features = convert(grouped.get(ResourceType.FEATURE));
        List<ResourceAccessItemResponse> elements = convert(grouped.get(ResourceType.ELEMENT));
        List<ResourceAccessItemResponse> denied = view.resources().stream()
                .filter(resource -> !resource.accessible())
                .map(this::toResponse)
                .sorted(Comparator.comparing(ResourceAccessItemResponse::code))
                .toList();
        return new UserResourceAccessResponse(menus, pages, features, elements, denied);
    }

    private List<ResourceAccessItemResponse> convert(Collection<ResourceAccessView> resources) {
        if (resources == null || resources.isEmpty()) {
            return List.of();
        }
        return resources.stream()
                .sorted(Comparator.comparing(ResourceAccessView::code))
                .map(this::toResponse)
                .toList();
    }

    private ResourceAccessItemResponse toResponse(ResourceAccessView resource) {
        List<String> allowed = resource.allowedActions().stream()
                .map(Enum::name)
                .sorted()
                .toList();
        List<String> denied = resource.deniedActions().stream()
                .map(Enum::name)
                .sorted()
                .toList();
        return new ResourceAccessItemResponse(
                resource.resourceId(),
                resource.type(),
                resource.code(),
                resource.displayName(),
                resource.parentId(),
                resource.displayOrder(),
                resource.accessible(),
                allowed,
                denied
        );
    }
}
