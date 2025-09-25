package com.tinyquest.hub.auth.service;

import com.tinyquest.hub.auth.domain.entity.PermissionEntity;
import com.tinyquest.hub.auth.domain.entity.ResourceEntity;
import com.tinyquest.hub.auth.domain.repository.PermissionRepository;
import com.tinyquest.hub.auth.domain.repository.RolePermissionRepository;
import com.tinyquest.hub.auth.domain.repository.UserRoleRepository;
import com.tinyquest.hub.shared.port.auth.PermissionQueryPort;
import com.tinyquest.hub.shared.port.auth.query.ResourceAccessView;
import com.tinyquest.hub.shared.port.auth.query.UserResourceAccessView;
import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.PermissionEffect;
import com.tinyquest.hub.shared.permission.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PermissionService implements PermissionQueryPort {

    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Cacheable(cacheNames = "user-permissions", key = "T(com.tinyquest.hub.auth.service.PermissionService).cacheKey(#userId, #scopes)")
    public UserResourceAccessView getResourcesForUser(Long userId, Set<ResourceType> scopes) {
        Set<ResourceType> effectiveScopes = resolveScopes(scopes);
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return UserResourceAccessView.empty(userId, effectiveScopes);
        }

        List<Long> permissionIds = rolePermissionRepository.findPermissionIdsByRoleIds(roleIds);
        if (permissionIds.isEmpty()) {
            return UserResourceAccessView.empty(userId, effectiveScopes);
        }

        List<PermissionEntity> permissions = permissionRepository.findAllByIdInWithResource(permissionIds);
        List<ResourceAccessView> resources = aggregateResources(permissions, effectiveScopes);
        return new UserResourceAccessView(userId, effectiveScopes, resources);
    }

    @Override
    @CacheEvict(cacheNames = "user-permissions", allEntries = true)
    public void evictAllUserPermissions(Long userId) {
        // 전체 캐시를 비우는 단순 전략입니다. 필요 시 사용자별 정밀 인밸리데이션으로 확장하세요.
    }

    private List<ResourceAccessView> aggregateResources(List<PermissionEntity> permissions, Set<ResourceType> scopes) {
        Map<Long, ResourceEntity> resourceById = new HashMap<>();
        Map<Long, EnumSet<PermissionAction>> allowMap = new HashMap<>();
        Map<Long, EnumSet<PermissionAction>> denyMap = new HashMap<>();

        for (PermissionEntity permission : permissions) {
            ResourceEntity resource = permission.getResource();
            if (resource == null || resource.getId() == null) {
                continue;
            }
            if (!scopes.contains(resource.getType())) {
                continue;
            }
            resourceById.putIfAbsent(resource.getId(), resource);
            if (permission.getEffect() == PermissionEffect.ALLOW) {
                allowMap.computeIfAbsent(resource.getId(), ignored -> EnumSet.noneOf(PermissionAction.class))
                        .add(permission.getAction());
            } else {
                denyMap.computeIfAbsent(resource.getId(), ignored -> EnumSet.noneOf(PermissionAction.class))
                        .add(permission.getAction());
            }
        }

        List<ResourceAccessView> resources = new ArrayList<>();
        for (Map.Entry<Long, ResourceEntity> entry : resourceById.entrySet()) {
            Long resourceId = entry.getKey();
            ResourceEntity resource = entry.getValue();
            Set<PermissionAction> allowed = snapshot(allowMap.get(resourceId));
            Set<PermissionAction> denied = snapshot(denyMap.get(resourceId));
            boolean accessible = !allowed.isEmpty();
            Long parentId = resource.getHierarchy() != null ? resource.getHierarchy().getParentId() : null;
            Integer order = resource.getHierarchy() != null ? resource.getHierarchy().getDisplayOrder() : null;
            resources.add(new ResourceAccessView(
                    resourceId,
                    resource.getType(),
                    resource.getCode(),
                    resource.getDisplayName(),
                    parentId,
                    order,
                    accessible,
                    allowed,
                    denied
            ));
        }

        resources.sort((left, right) -> {
            if (!Objects.equals(left.parentId(), right.parentId())) {
                return nullSafeCompare(left.parentId(), right.parentId());
            }
            int orderCompare = nullSafeCompare(left.displayOrder(), right.displayOrder());
            if (orderCompare != 0) {
                return orderCompare;
            }
            return left.code().compareToIgnoreCase(right.code());
        });
        return resources;
    }

    private static <T extends Comparable<T>> int nullSafeCompare(T left, T right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        return left.compareTo(right);
    }

    private static Set<PermissionAction> snapshot(Collection<PermissionAction> actions) {
        if (actions == null || actions.isEmpty()) {
            return Set.of();
        }
        return Set.copyOf(actions);
    }

    private static Set<ResourceType> resolveScopes(Set<ResourceType> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            return EnumSet.allOf(ResourceType.class);
        }
        return EnumSet.copyOf(scopes);
    }

    public static String cacheKey(Long userId, Set<ResourceType> scopes) {
        Set<ResourceType> effective = resolveScopes(scopes);
        String scopeKey = effective.stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(","));
        return "USER:" + userId + ':' + scopeKey;
    }
}
