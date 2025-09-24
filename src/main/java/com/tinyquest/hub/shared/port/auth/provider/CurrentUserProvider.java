package com.tinyquest.hub.shared.port.auth.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CurrentUserProvider {

    public UserIdentity getCurrentUser() throws AccessDeniedException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthenticated");
        }
        var principal = authentication.getPrincipal();
        if (principal instanceof UserIdentity authPrincipal) {
            return authPrincipal;
        }
        throw new AccessDeniedException("Invalid principal type");
    }
}
