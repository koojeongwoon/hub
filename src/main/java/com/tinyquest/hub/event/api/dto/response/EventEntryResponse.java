package com.tinyquest.hub.event.api.dto.response;

import com.tinyquest.hub.event.domain.entity.EventEntry;
import java.time.Instant;

public record EventEntryResponse(
        Long id,
        Long eventId,
        Long participantId,
        Instant createdAt
) {

    public static EventEntryResponse from(EventEntry entry) {
        return new EventEntryResponse(
                entry.getId(),
                entry.getEvent().getId(),
                entry.getParticipantId(),
                entry.getCreatedAt()
        );
    }
}
