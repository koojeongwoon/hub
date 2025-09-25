package com.tinyquest.hub.auth.support;

import com.tinyquest.hub.auth.domain.entity.PermissionEntity;
import com.tinyquest.hub.auth.domain.entity.ResourceEntity;
import com.tinyquest.hub.auth.domain.entity.ResourceHierarchy;
import com.tinyquest.hub.auth.domain.entity.RoleEntity;
import com.tinyquest.hub.auth.domain.entity.RolePermissionEntity;
import com.tinyquest.hub.auth.domain.entity.RolePermissionId;
import com.tinyquest.hub.auth.domain.repository.PermissionRepository;
import com.tinyquest.hub.auth.domain.repository.ResourceRepository;
import com.tinyquest.hub.auth.domain.repository.RolePermissionRepository;
import com.tinyquest.hub.auth.domain.repository.RoleRepository;
import com.tinyquest.hub.shared.constants.PermissionCodes;
import com.tinyquest.hub.shared.constants.ResourceCodes;
import com.tinyquest.hub.shared.constants.RoleCodes;
import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PermissionDataInitializer implements ApplicationRunner {

    private final ResourceRepository resourceRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ResourceEntity root = ensureResource(ResourceCodes.GROUP_APPLICATION, "애플리케이션", ResourceType.GROUP, null, 0, "애플리케이션 루트");
        ResourceEntity userMenu = ensureResource(ResourceCodes.MENU_USER_MANAGEMENT, "사용자 관리", ResourceType.MENU, root.getId(), 1, "사용자 관리 메뉴");
        ResourceEntity userPage = ensureResource(ResourceCodes.PAGE_USER_DIRECTORY, "사용자 목록", ResourceType.PAGE, userMenu.getId(), 1, "사용자 목록 화면");
        ResourceEntity userSearchFeature = ensureResource(ResourceCodes.FEATURE_USER_SEARCH, "사용자 검색", ResourceType.FEATURE, userPage.getId(), 1, "사용자 검색 기능");
        ResourceEntity roleManageFeature = ensureResource(ResourceCodes.FEATURE_USER_ROLE_MANAGEMENT, "사용자 권한 관리", ResourceType.FEATURE, userPage.getId(), 2, "사용자 권한 관리 기능");

        PermissionEntity menuView = ensurePermission(PermissionCodes.MENU_USER_MANAGEMENT_VIEW, "사용자 메뉴 조회", PermissionAction.VIEW, userMenu, "사용자 관리 메뉴 접근");
        PermissionEntity pageView = ensurePermission(PermissionCodes.PAGE_USER_DIRECTORY_VIEW, "사용자 목록 조회", PermissionAction.VIEW, userPage, "사용자 목록 화면 접근");
        PermissionEntity searchExecute = ensurePermission(PermissionCodes.FEATURE_USER_SEARCH_EXECUTE, "사용자 검색 수행", PermissionAction.EXECUTE, userSearchFeature, "사용자 검색 기능 사용");
        PermissionEntity roleUpdate = ensurePermission(PermissionCodes.FEATURE_USER_ROLE_MANAGE_UPDATE, "사용자 권한 수정", PermissionAction.UPDATE, roleManageFeature, "사용자 권한 관리 수행");

        RoleEntity adminRole = ensureRole(RoleCodes.ADMIN, "관리자", "모든 기능을 사용할 수 있는 관리자", 10, true);
        RoleEntity userRole = ensureRole(RoleCodes.USER, "일반 사용자", "기본 사용자 역할", 50, true);

        link(adminRole, menuView);
        link(adminRole, pageView);
        link(adminRole, searchExecute);
        link(adminRole, roleUpdate);

        link(userRole, menuView);
        link(userRole, pageView);
        link(userRole, searchExecute);
    }

    private ResourceEntity ensureResource(String code, String displayName, ResourceType type, Long parentId, int order, String description) {
        return resourceRepository.findByCode(code)
                .orElseGet(() -> resourceRepository.save(ResourceEntity.create(
                        code,
                        displayName,
                        description,
                        type,
                        parentId == null ? ResourceHierarchy.root(order) : ResourceHierarchy.of(parentId, order)
                )));
    }

    private PermissionEntity ensurePermission(String code, String displayName, PermissionAction action, ResourceEntity resource, String description) {
        return permissionRepository.findByCode(code)
                .orElseGet(() -> permissionRepository.save(PermissionEntity.allow(code, displayName, action, resource, description)));
    }

    private RoleEntity ensureRole(String code, String displayName, String description, int priority, boolean systemRole) {
        return roleRepository.findByCode(code)
                .orElseGet(() -> roleRepository.save(RoleEntity.create(code, displayName, description, priority, systemRole)));
    }

    private void link(RoleEntity role, PermissionEntity permission) {
        RolePermissionId id = new RolePermissionId(role.getId(), permission.getId());
        if (!rolePermissionRepository.existsById(id)) {
            rolePermissionRepository.save(RolePermissionEntity.link(role, permission));
        }
    }
}
