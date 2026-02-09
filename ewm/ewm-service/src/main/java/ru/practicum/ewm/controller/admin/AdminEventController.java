package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.event.EventState;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {

    private final EventService eventService;

    // 14 Поиск событий
    @GetMapping
    public List<EventFullDto> findEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Логирование входных параметров
        log.info("Запрос на поиск событий. Параметры: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        // Вызов сервиса с проверенными параметрами
        List<EventFullDto> events = eventService.findAdminEvents(
                users, states, categories, rangeStart, rangeEnd, from, size
        );

        log.info("Admin events response: {}", events);
        // Логирование результата
        log.info("Найдено {} событий", events.size());

        return events;
    }

    // 15 Редактирование данных события и его статуса (отклонение/публикация)
    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(
            @PathVariable Long eventId,
            @RequestBody UpdateEventAdminRequest dto
    ) {
        // Логирование запроса на обновление
        log.info("Запрос на обновление события. Event ID: {}", eventId);

        EventFullDto updatedEvent = eventService.updateEventByAdmin(eventId, dto);

        // Логирование результата обновления
        log.info("Событие с ID: {} успешно обновлено", eventId);

        return updatedEvent;
    }
}
