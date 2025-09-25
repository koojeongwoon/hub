package com.tinyquest.hub.auth.domain.repository;

import com.tinyquest.hub.auth.domain.entity.ResourceEntity;
import com.tinyquest.hub.shared.permission.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {

    @Query("select r from ResourceEntity r where r.type in :types")
    List<ResourceEntity> findAllByTypeIn(@Param("types") Collection<ResourceType> types);

    Optional<ResourceEntity> findByCode(String code);
}
