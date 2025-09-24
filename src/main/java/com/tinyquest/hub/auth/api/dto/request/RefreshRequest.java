package com.tinyquest.hub.auth.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank String refreshToken,
        String deviceFingerprint,
        String scope
) {}
