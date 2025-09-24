package com.tinyquest.hub.auth.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        String clientId,
        String deviceName,
        String deviceFingerprint,
        String scope
) {}
