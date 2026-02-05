package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto create(Long userId, NewEventDto dto);

    EventFullDto getByUserIdEventId(Long userId, Long eventId);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest dto);

    // 15 Редактирование данных события и его статуса (отклонение/публикация)
    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);

    // 14 Поиск событий
    List<EventFullDto> findAdminEvents(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    );

    // 16 Получение событий с возможностью фильтрации
    List<EventShortDto> findPublic(
            String text,
            List<Long> categoryIds,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            EventSort sort,
            int from,
            int size
    );

    // 17 Получение подробной информации об опубликованном событии по его идентификатору
    EventFullDto findPublicById(Long eventId);
}
