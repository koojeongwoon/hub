package com.tinyquest.hub.user.api.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long id, String email, String name, Integer age, LocalDateTime createdAt
) {}

