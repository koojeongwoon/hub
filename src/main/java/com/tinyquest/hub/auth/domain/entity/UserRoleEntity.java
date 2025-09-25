package com.tinyquest.hub.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_role",
        indexes = {
                @Index(name = "idx_user_role_user", columnList = "user_id"),
                @Index(name = "idx_user_role_role", columnList = "role_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoleEntity {

    @EmbeddedId
    private UserRoleId id;

    @Column(name = "assigned_at", nullable = false, columnDefinition = "DATETIME(3)")
    private java.time.Instant assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    private UserRoleEntity(UserRoleId id, RoleEntity role, java.time.Instant assignedAt) {
        this.id = id;
        this.role = role;
        this.assignedAt = assignedAt;
    }

    public static UserRoleEntity assign(Long userId, RoleEntity role, java.time.Instant assignedAt) {
        return new UserRoleEntity(new UserRoleId(userId, role.getId()), role, assignedAt);
    }
}
