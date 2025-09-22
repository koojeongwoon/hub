package com.tinyquest.hub.shared.port.user;

public record UserDTO(
        Long id,
        String username,
        String passwordHash,
        boolean enabled
) {}
