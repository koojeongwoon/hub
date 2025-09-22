package com.tinyquest.hub.user.api.dto.request;

import jakarta.validation.constraints.*;

public record UserCreateRequest(
        @NotBlank @Email String email,
        @NotBlank String password, // Add password field
        @NotBlank @Size(max = 50) String name,
        @PositiveOrZero @Max(150) Integer age
) {}
