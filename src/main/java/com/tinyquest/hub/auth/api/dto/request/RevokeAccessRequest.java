package com.tinyquest.hub.auth.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RevokeAccessRequest(
        @NotNull UUID jti,
        String reason
) {}
