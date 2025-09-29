package com.tinyquest.hub.event.domain.repository;

import com.tinyquest.hub.event.domain.entity.EventWinner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventWinnerRepository extends JpaRepository<EventWinner, Long> {
}
