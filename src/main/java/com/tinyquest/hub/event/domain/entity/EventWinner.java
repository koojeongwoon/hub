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
@Table(name = "event_winner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventWinner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id")
    private EventEntry entry;

    @Column(nullable = false)
    private Long prizeId;

    @Column(nullable = false)
    private Integer drawRound;

    @Column(nullable = false, columnDefinition = "DATETIME(3)")
    private Instant selectedAt;

    private EventWinner(Event event, EventEntry entry, Long prizeId, Integer drawRound) {
        this.event = event;
        this.entry = entry;
        this.prizeId = prizeId;
        this.drawRound = drawRound;
    }

    public static EventWinner create(Event event, EventEntry entry, Long prizeId, Integer drawRound) {
        return new EventWinner(event, entry, prizeId, drawRound);
    }

    @PrePersist
    void prePersist() {
        this.selectedAt = Instant.now();
    }
}
