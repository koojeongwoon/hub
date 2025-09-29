package com.tinyquest.hub.event.domain.repository;

import com.tinyquest.hub.event.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
