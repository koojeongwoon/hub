package com.tinyquest.hub.auth.security;

import com.tinyquest.hub.shared.port.auth.provider.UserIdentity;

public record AuthPrincipal(
        Long id,
        String username
) implements UserIdentity {}
