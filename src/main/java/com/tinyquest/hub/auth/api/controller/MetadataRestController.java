package com.tinyquest.hub.auth.api.controller;

import com.tinyquest.hub.auth.api.converter.PermissionMetadataConverter;
import com.tinyquest.hub.auth.api.document.MetadataRestControllerDoc;
import com.tinyquest.hub.auth.api.dto.response.UserResourceAccessResponse;
import com.tinyquest.hub.auth.security.AuthPrincipal;
import com.tinyquest.hub.auth.service.PermissionService;
import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.error.BusinessException;
import com.tinyquest.hub.shared.permission.ResourceType;
import com.tinyquest.hub.shared.port.auth.query.UserResourceAccessView;
import com.tinyquest.hub.shared.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MetadataRestController implements MetadataRestControllerDoc {

    private final PermissionService permissionService;
    private final PermissionMetadataConverter converter;

    @GetMapping("/resources/me")
    @Override
    public Response<UserResourceAccessResponse> getMyResources(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(name = "scopes", required = false) Set<ResourceType> scopes
    ) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.USER_AUTH_2001);
        }
        UserResourceAccessView view = permissionService.getResourcesForUser(principal.id(), scopes);
        return Response.Success.of(converter.convert(view));
    }
}
