package com.tinyquest.hub.event.domain.repository;

import com.tinyquest.hub.event.domain.entity.Event;
import com.tinyquest.hub.event.domain.entity.EventEntry;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventEntryRepository extends JpaRepository<EventEntry, Long> {

    Optional<EventEntry> findByEventAndParticipantId(Event event, Long participantId);
}
