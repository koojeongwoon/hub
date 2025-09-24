package com.tinyquest.hub.user.api.controller;

import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.error.BusinessException;
import com.tinyquest.hub.shared.port.auth.provider.CurrentUserProvider;
import com.tinyquest.hub.shared.response.ApiResponse;
import com.tinyquest.hub.shared.response.PageResponse;
import com.tinyquest.hub.shared.utils.SortWhitelist;
import com.tinyquest.hub.user.api.dto.request.UserCreateRequest;
import com.tinyquest.hub.user.api.dto.request.UserSearchRequest;
import com.tinyquest.hub.user.api.dto.request.UserUpdateRequest;
import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import com.tinyquest.hub.user.api.assembler.UserDetailAssembler;
import com.tinyquest.hub.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value="/api/users", produces = MediaTypes.HAL_JSON_VALUE)
public class UserRestController {
    private final UserService svc;
    private final UserDetailAssembler userDetailAssembler;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/me")
    public EntityModel<UserDetailResponse> get(
    ) {
        try {
            var principal = currentUserProvider.getCurrentUser();
            var v = svc.get(principal.id());
            return userDetailAssembler.toModel(v);
        } catch(Exception ex) {
            throw new RuntimeException("Access denied", ex);
        }
    }

    @GetMapping
    public PageResponse<UserDetailResponse> search(
            @Valid UserSearchRequest req,
            Pageable pageable
    ) {
        // 정렬 화이트리스트 적용(보안/안정성)
        Pageable safe =
                SortWhitelist.filter(
                        pageable,
                        List.of("createdAt","name","email","id"),
                        Sort.by(Sort.Direction.DESC, "createdAt")
                );
        return svc.search(req, safe);
    }

    @PostMapping("/register")
    public ApiResponse<Void> create(
            @Valid @RequestBody UserCreateRequest req
    ) {
        svc.create(req);
        return ApiResponse.Success.of();
    }

    @PutMapping
    public ApiResponse<Void> update(
            @Valid @RequestBody UserUpdateRequest req
    ) {
        var principal = currentUserProvider.getCurrentUser();
        svc.update(principal.id(), req);
        return ApiResponse.Success.of();
    }

    @DeleteMapping
    public ApiResponse<Void> delete() {
        var principal = currentUserProvider.getCurrentUser();

        svc.delete(principal.id());
        return ApiResponse.Success.of();
    }
}
