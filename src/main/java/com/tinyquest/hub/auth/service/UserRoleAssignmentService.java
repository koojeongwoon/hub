package com.tinyquest.hub.auth.service;

import com.tinyquest.hub.auth.domain.entity.RoleEntity;
import com.tinyquest.hub.auth.domain.entity.UserRoleEntity;
import com.tinyquest.hub.auth.domain.entity.UserRoleId;
import com.tinyquest.hub.auth.domain.repository.RoleRepository;
import com.tinyquest.hub.auth.domain.repository.UserRoleRepository;
import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.constants.RoleCodes;
import com.tinyquest.hub.shared.port.auth.PermissionQueryPort;
import com.tinyquest.hub.shared.port.auth.command.UserRoleAssignmentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleAssignmentService implements UserRoleAssignmentPort {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionQueryPort permissionQueryPort;
    private final Clock clock;

    @Override
    public void assignDefaultRoles(Long userId) {
        RoleEntity defaultRole = roleRepository.findByCode(RoleCodes.USER)
                .orElseThrow(() -> new IllegalStateException("Default role not configured"));
        UserRoleId id = new UserRoleId(userId, defaultRole.getId());
        if (!userRoleRepository.existsById(id)) {
            userRoleRepository.save(UserRoleEntity.assign(userId, defaultRole, now()));
        }
        permissionQueryPort.evictAllUserPermissions(userId);
    }

    @Override
    public void updateRoles(Long userId, Set<String> roleCodes) {
        Set<String> normalized = normalize(roleCodes);
        List<UserRoleEntity> existing = userRoleRepository.findAllByUserId(userId);

        if (normalized.isEmpty()) {
            if (!existing.isEmpty()) {
                userRoleRepository.deleteAll(existing);
                permissionQueryPort.evictAllUserPermissions(userId);
            }
            return;
        }

        List<RoleEntity> targets = roleRepository.findAllByCodeIn(normalized);
        if (targets.size() != normalized.size()) {
            throw new com.tinyquest.hub.shared.error.BusinessException(ErrorCode.AUTH_ROLE_NOT_FOUND_4001);
        }

        Map<Long, UserRoleEntity> existingByRoleId = existing.stream()
                .collect(Collectors.toMap(value -> value.getRole().getId(), Function.identity()));

        Set<Long> desiredRoleIds = targets.stream().map(RoleEntity::getId).collect(Collectors.toSet());

        List<UserRoleEntity> toDelete = existing.stream()
                .filter(entity -> !desiredRoleIds.contains(entity.getRole().getId()))
                .toList();
        if (!toDelete.isEmpty()) {
            userRoleRepository.deleteAllInBatch(toDelete);
        }

        for (RoleEntity target : targets) {
            if (!existingByRoleId.containsKey(target.getId())) {
                userRoleRepository.save(UserRoleEntity.assign(userId, target, now()));
            }
        }

        permissionQueryPort.evictAllUserPermissions(userId);
    }

    private Instant now() {
        return clock.instant();
    }

    private static Set<String> normalize(Set<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return Set.of();
        }
        return roleCodes.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    }
}
