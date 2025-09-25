package com.tinyquest.hub.user.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserRoleUpdateRequest(
        @NotNull
        @Size(max = 20)
        List<String> roleCodes
) {
}
