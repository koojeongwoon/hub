package com.tinyquest.hub.auth.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리소스 계층 구조(parentId, 정렬 순서)를 표현합니다.
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceHierarchy {

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "display_order")
    private Integer displayOrder;

    private ResourceHierarchy(Long parentId, Integer displayOrder) {
        this.parentId = parentId;
        this.displayOrder = displayOrder;
    }

    public static ResourceHierarchy root(Integer displayOrder) {
        return new ResourceHierarchy(null, displayOrder);
    }

    public static ResourceHierarchy of(Long parentId, Integer displayOrder) {
        return new ResourceHierarchy(parentId, displayOrder);
    }
}
