package com.tinyquest.hub.auth.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LogoutRequest(
        @NotNull UUID sessionId,
        String reason
) {}
