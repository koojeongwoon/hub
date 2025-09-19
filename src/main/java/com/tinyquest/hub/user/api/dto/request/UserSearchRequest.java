package com.tinyquest.hub.user.api.dto.request;

import jakarta.validation.constraints.Max;

public record UserSearchRequest(
        @Max(300) String q
) {
    public UserSearchRequest {
        q = (q == null) ? "" : q.trim();
    }
}
