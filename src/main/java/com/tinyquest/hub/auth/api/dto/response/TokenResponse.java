package com.tinyquest.hub.auth.api.dto.response;

import java.time.Instant;
import java.util.UUID;

public record TokenResponse(
        String tokenType,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        UUID sessionId
) {}
