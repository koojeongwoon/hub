package com.tinyquest.hub.auth.api.dto.response;

import java.util.List;

public record UserResourceAccessResponse(
        List<ResourceAccessItemResponse> menus,
        List<ResourceAccessItemResponse> pages,
        List<ResourceAccessItemResponse> features,
        List<ResourceAccessItemResponse> elements,
        List<ResourceAccessItemResponse> deniedResources
) {
}
