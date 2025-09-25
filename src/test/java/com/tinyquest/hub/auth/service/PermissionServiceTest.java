package com.tinyquest.hub.auth.service;

import com.tinyquest.hub.auth.domain.entity.PermissionEntity;
import com.tinyquest.hub.auth.domain.entity.ResourceEntity;
import com.tinyquest.hub.auth.domain.entity.ResourceHierarchy;
import com.tinyquest.hub.auth.domain.repository.PermissionRepository;
import com.tinyquest.hub.auth.domain.repository.RolePermissionRepository;
import com.tinyquest.hub.auth.domain.repository.UserRoleRepository;
import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.ResourceType;
import com.tinyquest.hub.shared.port.auth.query.ResourceAccessView;
import com.tinyquest.hub.shared.port.auth.query.UserResourceAccessView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private RolePermissionRepository rolePermissionRepository;
    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    @DisplayName("사용자 권한을 리소스 타입별로 묶어서 반환한다")
    void aggregateResourcesByAllowedAndDeniedActions() {
        Long userId = 10L;
        Long roleId = 20L;
        Long allowPermissionId = 30L;
        Long denyPermissionId = 31L;

        ResourceEntity dashboardMenu = ResourceEntity.create(
                "MENU_DASHBOARD",
                "대시보드",
                null,
                ResourceType.MENU,
                ResourceHierarchy.root(1)
        );
        ReflectionTestUtils.setField(dashboardMenu, "id", 100L);

        PermissionEntity allowView = PermissionEntity.allow(
                "MENU_DASHBOARD_VIEW",
                "대시보드 조회",
                PermissionAction.VIEW,
                dashboardMenu,
                null
        );
        ReflectionTestUtils.setField(allowView, "id", allowPermissionId);

        PermissionEntity denyApprove = PermissionEntity.deny(
                "MENU_DASHBOARD_APPROVE",
                "대시보드 승인 제한",
                PermissionAction.APPROVE,
                dashboardMenu,
                null
        );
        ReflectionTestUtils.setField(denyApprove, "id", denyPermissionId);

        when(userRoleRepository.findRoleIdsByUserId(userId)).thenReturn(List.of(roleId));
        when(rolePermissionRepository.findPermissionIdsByRoleIds(List.of(roleId)))
                .thenReturn(List.of(allowPermissionId, denyPermissionId));
        when(permissionRepository.findAllByIdInWithResource(List.of(allowPermissionId, denyPermissionId)))
                .thenReturn(List.of(allowView, denyApprove));

        UserResourceAccessView result = permissionService.getResourcesForUser(userId, Set.of(ResourceType.MENU));

        assertThat(result.resources()).hasSize(1);
        ResourceAccessView menu = result.resources().get(0);
        assertThat(menu.type()).isEqualTo(ResourceType.MENU);
        assertThat(menu.allowedActions()).containsExactly(PermissionAction.VIEW);
        assertThat(menu.deniedActions()).containsExactly(PermissionAction.APPROVE);
        assertThat(menu.accessible()).isTrue();
        assertThat(menu.displayOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("역할이 없는 사용자는 빈 결과를 반환한다")
    void emptyWhenUserHasNoRoles() {
        Long userId = 44L;
        when(userRoleRepository.findRoleIdsByUserId(userId)).thenReturn(List.of());

        UserResourceAccessView result = permissionService.getResourcesForUser(userId, Set.of(ResourceType.MENU));

        assertThat(result.resources()).isEmpty();
    }
}
