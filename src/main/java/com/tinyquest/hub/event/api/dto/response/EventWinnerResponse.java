package com.tinyquest.hub.event.api.dto.response;

import com.tinyquest.hub.event.domain.entity.EventWinner;
import java.time.Instant;

public record EventWinnerResponse(
        Long id,
        Long eventId,
        Long entryId,
        Long prizeId,
        Integer drawRound,
        Instant selectedAt
) {

    public static EventWinnerResponse from(EventWinner winner) {
        return new EventWinnerResponse(
                winner.getId(),
                winner.getEvent().getId(),
                winner.getEntry() != null ? winner.getEntry().getId() : null,
                winner.getPrizeId(),
                winner.getDrawRound(),
                winner.getSelectedAt()
        );
    }
}
