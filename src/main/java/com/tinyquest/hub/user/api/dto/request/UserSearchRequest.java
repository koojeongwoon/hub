package com.tinyquest.hub.user.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public record UserSearchRequest(
        @Size(max=300) String q
) {
    public UserSearchRequest {
        q = (q == null) ? "" : q.trim();
    }
}
