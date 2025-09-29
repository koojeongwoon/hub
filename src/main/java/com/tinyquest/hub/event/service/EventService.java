package com.tinyquest.hub.event.service;

import com.tinyquest.hub.event.application.EventLifecycleService;
import com.tinyquest.hub.event.domain.entity.Event;
import com.tinyquest.hub.event.domain.entity.EventEntry;
import com.tinyquest.hub.event.domain.entity.EventWinner;
import com.tinyquest.hub.event.domain.repository.EventEntryRepository;
import com.tinyquest.hub.event.domain.repository.EventRepository;
import com.tinyquest.hub.event.domain.repository.EventWinnerRepository;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventEntryRepository eventEntryRepository;
    private final EventWinnerRepository eventWinnerRepository;
    private final EventLifecycleService eventLifecycleService;

    public Event create(String name, String type) {
        Event event = Event.create(name, type);
        return eventRepository.save(event);
    }

    public Event publish(Long eventId, ChannelPreference preference, List<NotificationRecipient> recipients) {
        Event event = getEvent(eventId);
        if (!event.isPublished()) {
            event.publish();
            eventLifecycleService.notifyEventPublished(event, preference, recipients);
        }
        return event;
    }

    public EventEntry participate(Long eventId,
                                  Long participantId,
                                  ChannelPreference preference,
                                  List<NotificationRecipient> recipients) {
        Event event = getEvent(eventId);
        EventEntry entry = eventEntryRepository.findByEventAndParticipantId(event, participantId)
                .orElseGet(() -> eventEntryRepository.save(EventEntry.create(event, participantId)));
        eventLifecycleService.notifyEntrySubmitted(event, entry, preference, recipients);
        return entry;
    }

    public EventWinner selectWinner(Long eventId,
                                    Long entryId,
                                    Long prizeId,
                                    Integer drawRound,
                                    ChannelPreference preference,
                                    List<NotificationRecipient> recipients) {
        Event event = getEvent(eventId);
        EventEntry entry = eventEntryRepository.findById(entryId)
                .filter(it -> it.getEvent().equals(event))
                .orElseThrow(() -> new EntityNotFoundException("참여 내역을 찾을 수 없습니다."));

        EventWinner winner = eventWinnerRepository.save(EventWinner.create(event, entry, prizeId, drawRound));
        eventLifecycleService.notifyWinnerSelected(event, winner, preference, recipients);
        return winner;
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("행사를 찾을 수 없습니다."));
    }
}
