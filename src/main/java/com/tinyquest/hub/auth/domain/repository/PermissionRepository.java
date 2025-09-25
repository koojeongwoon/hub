package com.tinyquest.hub.auth.domain.repository;

import com.tinyquest.hub.auth.domain.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    @Query("select distinct p from PermissionEntity p join fetch p.resource where p.id in :ids")
    List<PermissionEntity> findAllByIdInWithResource(@Param("ids") Collection<Long> ids);

    Optional<PermissionEntity> findByCode(String code);
}
