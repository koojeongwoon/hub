package com.tinyquest.hub.event.domain.entity;

import com.tinyquest.hub.event.domain.EventStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 50)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status;

    @Column(columnDefinition = "DATETIME(3)")
    private Instant publishedAt;

    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME(3)")
    private Instant createdAt;

    @Column(nullable = false, columnDefinition = "DATETIME(3)")
    private Instant updatedAt;

    private Event(String name, String type) {
        this.name = name;
        this.type = type;
        this.status = EventStatus.DRAFT;
    }

    public static Event create(String name, String type) {
        return new Event(name, type);
    }

    public boolean isDraft() {
        return EventStatus.DRAFT.equals(status);
    }

    public boolean isPublished() {
        return EventStatus.PUBLISHED.equals(status);
    }

    public void publish() {
        if (!isDraft()) {
            throw new IllegalStateException("행사는 DRAFT 상태에서만 공개할 수 있습니다.");
        }
        this.status = EventStatus.PUBLISHED;
        this.publishedAt = Instant.now();
    }

    public void close() {
        if (!isPublished()) {
            throw new IllegalStateException("행사는 PUBLISHED 상태에서만 종료할 수 있습니다.");
        }
        this.status = EventStatus.CLOSED;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
