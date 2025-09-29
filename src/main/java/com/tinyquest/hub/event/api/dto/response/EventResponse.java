package com.tinyquest.hub.event.api.dto.response;

import com.tinyquest.hub.event.domain.entity.Event;
import com.tinyquest.hub.event.domain.EventStatus;
import java.time.Instant;

public record EventResponse(
        Long id,
        String name,
        String type,
        EventStatus status,
        Instant publishedAt
) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getType(),
                event.getStatus(),
                event.getPublishedAt()
        );
    }
}
