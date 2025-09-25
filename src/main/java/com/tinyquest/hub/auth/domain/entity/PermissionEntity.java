package com.tinyquest.hub.auth.domain.entity;

import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.PermissionEffect;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "permission",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_permission_code", columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_permission_resource", columnList = "resource_id"),
                @Index(name = "idx_permission_action", columnList = "action")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 150)
    private String code;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 40)
    private PermissionAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "effect", nullable = false, length = 20)
    private PermissionEffect effect;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private ResourceEntity resource;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(3)")
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(3)")
    private Instant updatedAt;

    private PermissionEntity(
            String code,
            String displayName,
            PermissionAction action,
            PermissionEffect effect,
            ResourceEntity resource,
            String description,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.code = code;
        this.displayName = displayName;
        this.action = action;
        this.effect = effect;
        this.resource = resource;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PermissionEntity allow(
            String code,
            String displayName,
            PermissionAction action,
            ResourceEntity resource,
            String description
    ) {
        Instant now = Instant.now();
        return new PermissionEntity(code, displayName, action, PermissionEffect.ALLOW, resource, description, now, now);
    }

    public static PermissionEntity deny(
            String code,
            String displayName,
            PermissionAction action,
            ResourceEntity resource,
            String description
    ) {
        Instant now = Instant.now();
        return new PermissionEntity(code, displayName, action, PermissionEffect.DENY, resource, description, now, now);
    }
}
