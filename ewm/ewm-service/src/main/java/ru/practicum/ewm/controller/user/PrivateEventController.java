package ru.practicum.ewm.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    // 6 Получение событий добавленных текущим пользователем
    @GetMapping
    public List<EventShortDto> getUserEvents(
            @Positive @PathVariable Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("User with id={} is retrieving events with pagination. From: {}, Size: {}", userId, from, size);
        List<EventShortDto> events = eventService.getUserEvents(
                userId,
                from,
                size
        );

        log.info("Retrieved {} events for user with id={}.", events.size(), userId);
        return events;
    }

    // 7 Добавление нового события
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(
            @Positive @PathVariable Long userId,
            @Valid @RequestBody NewEventDto dto
    ) {
        log.info("User with id={} is creating a new event.", userId);
        EventFullDto event = eventService.create(userId, dto);

        log.info("Event created successfully with id={}", event.getId());
        return event;
    }

    // 8 Получение полной информации о событии добавленном текущим пользователем
    @GetMapping("/{eventId}")
    public EventFullDto getByUserIdEventId(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId
    ) {
        log.info("User with id={} is event with id={}.", userId, eventId);
        EventFullDto event = eventService.getByUserIdEventId (userId, eventId);
        return event;
    }

    // 9 Изменение события добавленного текущим пользователем
    @PatchMapping("/{eventId}")
    public EventFullDto update(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody(required = false) UpdateEventUserRequest dto
    ) {
        log.info("User with id={} is updating event with id={}.", userId, eventId);
        EventFullDto updatedEvent = eventService.update(userId, eventId, dto);

        log.info("Event with id={} updated successfully.", eventId);
        return updatedEvent;
    }
}
