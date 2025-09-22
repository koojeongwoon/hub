package com.tinyquest.hub.auth.security;

public record AuthPrincipal(
        Long id,
        String username
) {}