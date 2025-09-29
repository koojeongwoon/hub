package com.tinyquest.hub.event.api.controller;

import com.tinyquest.hub.event.api.document.EventRestControllerDoc;
import com.tinyquest.hub.event.api.dto.request.EventCreateRequest;
import com.tinyquest.hub.event.api.dto.request.EventParticipationRequest;
import com.tinyquest.hub.event.api.dto.request.EventPublishRequest;
import com.tinyquest.hub.event.api.dto.request.EventWinnerRequest;
import com.tinyquest.hub.event.api.dto.response.EventEntryResponse;
import com.tinyquest.hub.event.api.dto.response.EventResponse;
import com.tinyquest.hub.event.api.dto.response.EventWinnerResponse;
import com.tinyquest.hub.event.domain.entity.Event;
import com.tinyquest.hub.event.domain.entity.EventEntry;
import com.tinyquest.hub.event.domain.entity.EventWinner;
import com.tinyquest.hub.event.service.EventService;
import com.tinyquest.hub.shared.constants.ResourceCodes;
import com.tinyquest.hub.shared.permission.PermissionAction;
import com.tinyquest.hub.shared.permission.RequirePermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventRestController implements EventRestControllerDoc {

    private final EventService eventService;

    @Override
    @PostMapping
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_EVENT_MANAGEMENT, actions = PermissionAction.CREATE)
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventCreateRequest request) {
        Event event = eventService.create(request.name(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(event));
    }

    @Override
    @PostMapping("/{eventId}/publish")
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_EVENT_MANAGEMENT, actions = PermissionAction.UPDATE)
    public EventResponse publish(@PathVariable Long eventId,
                                 @Valid @RequestBody EventPublishRequest request) {
        Event event = eventService.publish(eventId, request.toChannelPreference(), request.toRecipients());
        return EventResponse.from(event);
    }

    @Override
    @PostMapping("/{eventId}/entries")
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_EVENT_PARTICIPATION, actions = PermissionAction.CREATE)
    public EventEntryResponse participate(@PathVariable Long eventId,
                                          @Valid @RequestBody EventParticipationRequest request) {
        EventEntry entry = eventService.participate(
                eventId,
                request.participantId(),
                request.toChannelPreference(),
                request.toRecipients()
        );
        return EventEntryResponse.from(entry);
    }

    @Override
    @PostMapping("/{eventId}/winners")
    @RequirePermission(resourceCode = ResourceCodes.FEATURE_EVENT_WINNER_MANAGEMENT, actions = PermissionAction.APPROVE)
    public EventWinnerResponse selectWinner(@PathVariable Long eventId,
                                            @Valid @RequestBody EventWinnerRequest request) {
        EventWinner winner = eventService.selectWinner(
                eventId,
                request.entryId(),
                request.prizeId(),
                request.drawRound(),
                request.toChannelPreference(),
                request.toRecipients()
        );
        return EventWinnerResponse.from(winner);
    }
}
