package com.tinyquest.hub.auth.domain.repository;

import com.tinyquest.hub.auth.domain.entity.UserRoleEntity;
import com.tinyquest.hub.auth.domain.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {

    @Query("select ur.id.roleId from UserRoleEntity ur where ur.id.userId = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    @Query("select ur from UserRoleEntity ur join fetch ur.role where ur.id.userId = :userId")
    List<UserRoleEntity> findAllByUserId(@Param("userId") Long userId);
}
