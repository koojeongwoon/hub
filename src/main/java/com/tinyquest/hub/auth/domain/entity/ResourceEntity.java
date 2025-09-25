package com.tinyquest.hub.auth.domain.entity;

import com.tinyquest.hub.shared.permission.ResourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "resource",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_resource_code", columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_resource_type", columnList = "resource_type"),
                @Index(name = "idx_resource_parent", columnList = "parent_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 120)
    private String code;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 30)
    private ResourceType type;

    @Embedded
    private ResourceHierarchy hierarchy;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(3)")
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(3)")
    private Instant updatedAt;

    private ResourceEntity(
            String code,
            String displayName,
            String description,
            ResourceType type,
            ResourceHierarchy hierarchy,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.hierarchy = hierarchy;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ResourceEntity create(
            String code,
            String displayName,
            String description,
            ResourceType type,
            ResourceHierarchy hierarchy
    ) {
        Instant now = Instant.now();
        return new ResourceEntity(code, displayName, description, type, hierarchy, true, now, now);
    }

    public void updateHierarchy(Long parentId, Integer displayOrder) {
        this.hierarchy = ResourceHierarchy.of(parentId, displayOrder);
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }
}
