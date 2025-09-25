package com.tinyquest.hub.user.api.controller;

import com.tinyquest.hub.shared.port.auth.provider.CurrentUserProvider;
import com.tinyquest.hub.shared.response.Response;
import com.tinyquest.hub.shared.utils.SortWhitelist;
import com.tinyquest.hub.user.api.dto.request.UserCreateRequest;
import com.tinyquest.hub.user.api.dto.request.UserSearchRequest;
import com.tinyquest.hub.user.api.dto.request.UserUpdateRequest;
import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import com.tinyquest.hub.user.api.document.UserRestControllerDoc;
import com.tinyquest.hub.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
