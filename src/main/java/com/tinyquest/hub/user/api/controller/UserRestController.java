package com.tinyquest.hub.user.api.controller;

import com.tinyquest.hub.shared.constants.ResourceCodes;
import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.RequirePermission;
import com.tinyquest.hub.shared.port.auth.provider.CurrentUserProvider;
import com.tinyquest.hub.shared.response.Response;
import com.tinyquest.hub.shared.utils.SortWhitelist;
import com.tinyquest.hub.user.api.document.UserRestControllerDoc;
import com.tinyquest.hub.user.api.dto.request.UserCreateRequest;
import com.tinyquest.hub.user.api.dto.request.UserSearchRequest;
import com.tinyquest.hub.user.api.dto.request.UserUpdateRequest;
import com.tinyquest.hub.user.api.dto.request.UserRoleUpdateRequest;
import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import com.tinyquest.hub.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
public class UserRestController implements UserRestControllerDoc {
    private final UserService svc;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/me")
    @Override
    public UserDetailResponse get() {
        var principal = currentUserProvider.getCurrentUser();
        return getById(principal.id());
    }

    @GetMapping("/{id}")
    @Override
    public UserDetailResponse getById(@PathVariable Long id) {
        assertOwnership(id);
        return svc.get(id);
    }

    @GetMapping
    @Override
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_USER_SEARCH, actions = PermissionAction.EXECUTE)
    public Page<UserDetailResponse> search(
            @Valid UserSearchRequest req,
            Pageable pageable
    ) {
        Pageable safe = SortWhitelist.filter(
                pageable,
                List.of("createdAt", "name", "email", "id"),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return svc.search(req, safe);
    }

    @PutMapping("/{id}/roles")
    @Override
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_USER_ROLE_MANAGEMENT, actions = PermissionAction.UPDATE)
    public Response<Void> updateRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateRequest req
    ) {
        Set<String> roleCodes = new HashSet<>(req.roleCodes());
        svc.updateRoles(id, roleCodes);
        return Response.Success.of();
    }

    @PostMapping("/register")
    @Override
    public Response<Void> create(@Valid @RequestBody UserCreateRequest req) {
        svc.create(req);
        return Response.Success.of();
    }

    @PutMapping
    @Override
    public Response<Void> update(@Valid @RequestBody UserUpdateRequest req) {
        var principal = currentUserProvider.getCurrentUser();
        svc.update(principal.id(), req);
        return Response.Success.of();
    }

    @DeleteMapping
    @Override
    public Response<Void> delete() {
        var principal = currentUserProvider.getCurrentUser();
        svc.delete(principal.id());
        return Response.Success.of();
    }

    private void assertOwnership(Long id) {
        var principal = currentUserProvider.getCurrentUser();
        if (!principal.id().equals(id)) {
            throw new AccessDeniedException("Cannot access another user's profile");
        }
    }
}
