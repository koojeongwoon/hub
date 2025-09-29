package com.tinyquest.hub.event.config;

import com.tinyquest.hub.event.domain.entity.Event;
import com.tinyquest.hub.event.domain.entity.EventEntry;
import com.tinyquest.hub.event.domain.entity.EventWinner;
import com.tinyquest.hub.event.domain.repository.EventEntryRepository;
import com.tinyquest.hub.event.domain.repository.EventRepository;
import com.tinyquest.hub.event.domain.repository.EventWinnerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class EventSeedInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(EventSeedInitializer.class);

    private final EventRepository eventRepository;
    private final EventEntryRepository eventEntryRepository;
    private final EventWinnerRepository eventWinnerRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (eventRepository.count() > 0) {
            return;
        }

        Event springDraw = Event.create("봄맞이 럭키드로", "DRAW");
        springDraw.publish();
        eventRepository.save(springDraw);

        EventEntry entryKim = eventEntryRepository.save(EventEntry.create(springDraw, 1001L));
        eventEntryRepository.save(EventEntry.create(springDraw, 1002L));
        eventWinnerRepository.save(EventWinner.create(springDraw, entryKim, 501L, 1));

        Event survey = Event.create("여름 만족도 조사", "SURVEY");
        eventRepository.save(survey);
        survey.publish();
        survey.close();
        eventEntryRepository.save(EventEntry.create(survey, 1003L));

        log.info("Seeded demo events for local/dev profiles: {}", List.of(springDraw.getName(), survey.getName()));
    }
}
