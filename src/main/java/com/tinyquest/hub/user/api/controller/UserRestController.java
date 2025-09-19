package com.tinyquest.hub.user.api.controller;

import com.tinyquest.hub.shared.response.ApiResponse;
import com.tinyquest.hub.shared.response.PageResponse;
import com.tinyquest.hub.shared.utils.SortWhitelist;
import com.tinyquest.hub.user.api.dto.request.UserCreateRequest;
import com.tinyquest.hub.user.api.dto.request.UserSearchRequest;
import com.tinyquest.hub.user.api.dto.request.UserUpdateRequest;
import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import com.tinyquest.hub.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserRestController {
    private final UserService svc;

    @GetMapping("/{id}")
    public UserDetailResponse get(@PathVariable Long id) {
        return svc.get(id);
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

    @PostMapping
    public ApiResponse<Void> create(
            @Valid @RequestBody UserCreateRequest req
    ) {
        svc.create(req);
        return ApiResponse.Success.of();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest req
    ) {
        svc.update(id, req);
        return ApiResponse.Success.of();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id
    ) {
        svc.delete(id);
        return ApiResponse.Success.of();
    }
}
