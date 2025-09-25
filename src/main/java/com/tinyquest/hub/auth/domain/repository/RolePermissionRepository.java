package com.tinyquest.hub.auth.domain.repository;

import com.tinyquest.hub.auth.domain.entity.RolePermissionEntity;
import com.tinyquest.hub.auth.domain.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, RolePermissionId> {

    @Query("select rp.id.permissionId from RolePermissionEntity rp where rp.id.roleId in :roleIds")
    List<Long> findPermissionIdsByRoleIds(@Param("roleIds") Collection<Long> roleIds);
}
