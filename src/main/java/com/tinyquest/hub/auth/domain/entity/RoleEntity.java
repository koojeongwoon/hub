package com.tinyquest.hub.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "role",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_code", columnNames = "code")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 80)
    private String code;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "is_system_role", nullable = false)
    private boolean systemRole;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(3)")
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(3)")
    private Instant updatedAt;

    private RoleEntity(
            String code,
            String displayName,
            String description,
            Integer priority,
            boolean systemRole,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.priority = priority;
        this.systemRole = systemRole;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RoleEntity create(
            String code,
            String displayName,
            String description,
            Integer priority,
            boolean systemRole
    ) {
        Instant now = Instant.now();
        return new RoleEntity(code, displayName, description, priority, systemRole, true, now, now);
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }
}
