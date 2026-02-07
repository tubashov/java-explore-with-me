package ru.practicum.ewm.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

import static ru.practicum.ewm.dto.event.EventState.PUBLISHED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    @Value("${spring.application.name:ewm-service}")
    private String appName;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    private void validateLength(String fieldName, String value, int min, int max) {
        if (value == null) return;

        int len = value.length();

        if (len < min) {
            throw new BadRequestException(
                    fieldName + " must be at least " + min + " characters long"
            );
        }

        if (len > max) {
            throw new BadRequestException(
                    fieldName + " must be no more than " + max + " characters long"
            );
        }
    }

    // 6 Получение событий, добавленных текущим пользователем
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        log.debug("Finding all events for userId={}, from={}, size={}", userId, from, size);

        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters: from=" + from + ", size=" + size
            );
        }

        PageRequest page = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);

        return events.stream()
                .map(EventMapper::toShortDto)
                .toList();
    }

    // 7 Добавление нового события
    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto dto) {
        log.info("Creating event by userId={} with data: {}", userId, dto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now())) {
            log.error("Event date cannot be in the past for userId={}", userId);
            throw new BadRequestException("Event date cannot be in the past");
        }

        Category categoryMapper = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));

        Event event = EventMapper.toEntity(dto, user);
        event.setCategory(categoryMapper);
        event.setInitiator(user);
        event.setState(EventState.PENDING); // Устанавливаем начальное состояние

        log.debug("""
                        EVENT BEFORE SAVE:
                        title length={}
                        annotation length={}
                        description length={}
                        state={}
                        """,
                event.getTitle().length(),
                event.getAnnotation().length(),
                event.getDescription().length(),
                event.getState()
        );

        EventFullDto result = EventMapper.toFullDto(eventRepository.save(event));
        log.info("Event created successfully with id={}", result.getId());
        return result;
    }

    // 8 Получение полной информации о событии добавленном текущим пользователем
    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByUserIdEventId(Long userId, Long eventId) {
        log.info("Get event with id={} added by userId={}", eventId, userId);

        // проверка пользователя НЕ обязательна
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%d not found for userId=%d", eventId, userId)
                ));
        return EventMapper.toFullDto(event);
    }

    // 9 Изменение события добавленного текущим пользователем
    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest dto) {
        log.info("Updating event with eventId={} by userId={}", eventId, userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            log.error("User with id={} is not the initiator of the event with id={}", userId, eventId);
            throw new ConflictException("User is not the initiator of the event");
        }

        if (event.getState() == PUBLISHED) {
            log.error("Published event with id={} cannot be changed", eventId);
            throw new ConflictException("Published event cannot be changed");
        }

        if (dto == null) {
            log.warn("Received empty update request for event id={}", eventId);
            return EventMapper.toFullDto(event);
        }

        validateLength("Title", dto.getTitle(), 3, 120);
        validateLength("Annotation", dto.getAnnotation(), 20, 2000);
        validateLength("Description", dto.getDescription(), 20, 7000);

        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now())) {
            log.error("Event date cannot be in the past for event id={}", eventId);
            throw new BadRequestException("Event date cannot be in the past");
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        EventMapper.updateEvent(event, dto);

        EventFullDto result = EventMapper.toFullDto(eventRepository.save(event));
        log.info("Event with id={} updated successfully", result.getId());
        return result;
    }

    // 14 Поиск событий
    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> findAdminEvents(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    ) {
        log.info("""
                        Admin search events:
                        users={}
                        states={}
                        categories={}
                        rangeStart={}
                        rangeEnd={}
                        from={}
                        size={}
                        """,
                users, states, categories, rangeStart, rangeEnd, from, size
        );

        boolean useUsers = users != null && !users.isEmpty();
        boolean useStates = states != null && !states.isEmpty();
        boolean useCategories = categories != null && !categories.isEmpty();

        if (rangeStart == null) {
            rangeStart = LocalDateTime.of(1970, 1, 1, 0, 0);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(2100, 1, 1, 0, 0);
        }

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Event> page = eventRepository.findAdminEvents(
                users, useUsers,
                states, useStates,
                categories, useCategories,
                rangeStart,
                rangeEnd,
                pageable
        );

        log.info("Admin events found: {}", page.getTotalElements());

        return page.getContent().stream()
                .map(EventMapper::toFullDto)
                .toList();
    }

    // 15 Редактирование данных события и его статуса (отклонение/публикация)
    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        log.info("Admin updating event with eventId={}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event with id={} was not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        validateLength("Title", dto.getTitle(), 3, 120);
        validateLength("Annotation", dto.getAnnotation(), 20, 2000);
        validateLength("Description", dto.getDescription(), 20, 7000);

        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event date cannot be in the past");
        }

        // 1. Обновляем данные события (без бизнес-валидации)
        EventMapper.updateEventByAdmin(event, dto);

        // 2. Обрабатываем изменение статуса
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {

                case PUBLISH_EVENT:
                    // событие можно публиковать только из PENDING
                    if (event.getState() != EventState.PENDING) {
                        log.error("Only pending events can be published for event id={}", eventId);
                        throw new ConflictException("Only pending events can be published");
                    }

                    // дата события должна быть минимум через час от момента публикации
                    if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                        log.error("Event date must be at least 1 hour in the future for event id={}", eventId);
                        throw new ConflictException("Event date must be at least 1 hour in the future");
                    }

                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    log.info("Event with id={} published", eventId);
                    break;

                case REJECT_EVENT:
                    // нельзя отклонить опубликованное событие
                    if (event.getState() == PUBLISHED) {
                        log.error("Published event cannot be rejected for event id={}", eventId);
                        throw new ConflictException("Published event cannot be rejected");
                    }

                    event.setState(EventState.CANCELED);
                    log.info("Event with id={} rejected", eventId);
                    break;
            }
        }

        Event savedEvent = eventRepository.save(event);
        log.info("Event with id={} updated successfully", eventId);

        return EventMapper.toFullDto(savedEvent);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For"); // если есть прокси
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0]; // если несколько через прокси
    }

    // 16 Получение событий с возможностью фильтрации
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findPublic(
            String text,
            List<Long> categoryIds,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            EventSort sort,
            int from,
            int size,
            HttpServletRequest request
    ) {
        log.debug("Public search: text={}, categories={}, paid={}, onlyAvailable={}, from={}, size={}",
                text, categoryIds, paid, onlyAvailable, from, size);

        from = Math.max(0, from);
        size = Math.max(1, size);
        text = (text != null && text.isBlank()) ? null : text;
        categoryIds = (categoryIds != null && categoryIds.isEmpty()) ? null : categoryIds;
        onlyAvailable = Objects.requireNonNullElse(onlyAvailable, false);
        rangeStart = Objects.requireNonNullElse(rangeStart,
                LocalDateTime.of(1970, 1, 1, 0, 0));
        rangeEnd = Objects.requireNonNullElse(rangeEnd,
                LocalDateTime.of(2100, 1, 1, 0, 0));

        if (rangeStart.isAfter(rangeEnd)) {
            log.error("Invalid date range: rangeStart {} is after rangeEnd {}", rangeStart, rangeEnd);
            throw new BadRequestException("RangeStart must be before rangeEnd");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());

        Page<Event> page = (text == null)
                ? eventRepository.searchPublicWithoutText(categoryIds, paid, rangeStart, rangeEnd, PUBLISHED, pageable)
                : eventRepository.searchPublicWithText(
                        text,
                        categoryIds,
                        paid,
                        rangeStart,
                        rangeEnd,
                        PUBLISHED,
                        pageable);

        List<Event> events = onlyAvailable
                ? page.getContent().stream()
                .filter(e -> e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit())
                .toList()
                : page.getContent();

        log.info("Found {} events after filtering", events.size());

        Map<String, Long> views = new HashMap<>();
        if (!events.isEmpty()) {
            try {
                String ip = getClientIp(request);
                events.forEach(e -> statsClient.saveHit(
                        EndpointHitDto.builder()
                                .app(appName)
                                .uri("/events/" + e.getId())
                                .timestamp(LocalDateTime.now())
                                .ip(ip)
                                .build()
                ));
                log.debug("Saved hits for {} events", events.size());

                statsClient.getStats(rangeStart, LocalDateTime.now(), true,
                        events.stream().map(e -> "/events/" + e.getId()).toList()
                ).forEach(v -> views.put(v.getUri(), v.getHits()));

            } catch (Exception e) {
                log.warn("Stats service unavailable: {}", e.getMessage());
            }
        }

        if (sort == EventSort.VIEWS) {
            events = events.stream()
                    .sorted(Comparator.comparing(e -> views.getOrDefault("/events/" + e.getId(),
                            0L), Comparator.reverseOrder()))
                    .toList();
            log.debug("Sorted events by views");
        }

        return events.stream()
                .map(e -> {
                    EventShortDto dto = EventMapper.toShortDto(e);
                    dto.setViews(views.getOrDefault("/events/" + e.getId(), 0L));
                    return dto;
                })
                .toList();
    }

    // 17 Получение подробной информации об опубликованном событии по его идентификатору
    @Override
    @Transactional(readOnly = true)
    public EventFullDto findPublicById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> {
                    log.error("Event with id={} not found", eventId);
                    return new NotFoundException("Event not found");
                });

        String ip = getClientIp(request);
        statsClient.saveHit(EndpointHitDto.builder()
                .app(appName)
                .uri("/events/" + eventId)
                .timestamp(LocalDateTime.now())
                .ip(ip)
                .build());
        log.debug("Saved hit for eventId={}", eventId);

        Long views = statsClient.getStats(
                        event.getCreatedOn() != null ? event.getCreatedOn() :
                                LocalDateTime.of(1970, 1, 1, 0, 0),
                        LocalDateTime.now(),
                        true,
                        List.of("/events/" + eventId)
                ).stream().findFirst()
                .map(ViewStatsDto::getHits)
                .orElse(0L);

        EventFullDto dto = EventMapper.toFullDto(event);
        dto.setViews(views);
        log.info("Returning eventId={} with {} views", eventId, views);
        return dto;
    }
}
