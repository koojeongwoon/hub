package com.tinyquest.hub.event.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Long participantId;

    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME(3)")
    private Instant createdAt;

    private EventEntry(Event event, Long participantId) {
        this.event = event;
        this.participantId = participantId;
    }

    public static EventEntry create(Event event, Long participantId) {
        return new EventEntry(event, participantId);
    }

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }
}
