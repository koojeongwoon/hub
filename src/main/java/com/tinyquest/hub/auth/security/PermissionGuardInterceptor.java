package com.tinyquest.hub.auth.security;

import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.RequirePermission;
import com.tinyquest.hub.shared.permission.ResourceType;
import com.tinyquest.hub.shared.port.auth.PermissionQueryPort;
import com.tinyquest.hub.shared.port.auth.query.ResourceAccessView;
import com.tinyquest.hub.shared.port.auth.query.UserResourceAccessView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PermissionGuardInterceptor implements HandlerInterceptor {

    private static final String ATTRIBUTE_KEY = PermissionGuardInterceptor.class.getName() + ".CURRENT";

    private final PermissionQueryPort permissionQueryPort;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        RequirePermission definition = resolveDefinition(handler);
        if (definition == null) {
            return true;
        }

        AuthPrincipal principal = resolvePrincipal();
        Set<ResourceType> scopes = resolveScopes(definition);
        UserResourceAccessView view = lookupCachedView(request, principal.id(), scopes);

        ResourceAccessView resource = view.resources().stream()
                .filter(r -> Objects.equals(r.code(), definition.resourceCode()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Resource not accessible"));

        for (PermissionAction action : definition.actions()) {
            if (!resource.allowedActions().contains(action) || resource.deniedActions().contains(action)) {
                throw new AccessDeniedException("Action denied");
            }
        }

        if (!resource.accessible()) {
            throw new AccessDeniedException("Resource not accessible");
        }

        return true;
    }

    private UserResourceAccessView lookupCachedView(HttpServletRequest request, Long userId, Set<ResourceType> scopes) {
        @SuppressWarnings("unchecked")
        Map<Long, UserResourceAccessView> cache = (Map<Long, UserResourceAccessView>) request.getAttribute(ATTRIBUTE_KEY);
        if (cache == null) {
            cache = new ConcurrentHashMap<>();
            request.setAttribute(ATTRIBUTE_KEY, cache);
        }
        return cache.computeIfAbsent(userId, ignored -> permissionQueryPort.getResourcesForUser(userId, scopes));
    }

    private RequirePermission resolveDefinition(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return null;
        }
        RequirePermission methodDefinition = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequirePermission.class);
        if (methodDefinition != null) {
            return methodDefinition;
        }
        return AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RequirePermission.class);
    }

    private static Set<ResourceType> resolveScopes(RequirePermission definition) {
        ResourceType[] scopes = definition.scopes();
        if (scopes == null || scopes.length == 0) {
            return EnumSet.noneOf(ResourceType.class);
        }
        return EnumSet.copyOf(Arrays.asList(scopes));
    }

    private static AuthPrincipal resolvePrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal)) {
            throw new AccessDeniedException("Unauthenticated principal");
        }
        return principal;
    }
}
