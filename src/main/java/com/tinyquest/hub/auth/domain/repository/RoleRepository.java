package com.tinyquest.hub.auth.domain.repository;

import com.tinyquest.hub.auth.domain.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByCode(String code);

    List<RoleEntity> findAllByCodeIn(Collection<String> codes);
}
