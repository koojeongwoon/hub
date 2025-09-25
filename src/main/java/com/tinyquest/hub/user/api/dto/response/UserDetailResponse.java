package com.tinyquest.hub.user.api.dto.response;

import java.time.Instant;

public record UserDetailResponse(
        Long id,
        String email,
        String name,
        Integer age,
        Instant createdAt
) {}
