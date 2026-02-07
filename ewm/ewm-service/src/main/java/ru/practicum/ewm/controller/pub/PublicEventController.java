package ru.practicum.ewm.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.EventSort;
import ru.practicum.ewm.service.EventService;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    // 16 Получение событий с возможностью фильтрации
    @GetMapping
    public List<EventShortDto> findPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        statsClient.saveHit(
                EndpointHitDto.builder()
                        .app("ewm-service")
                        .uri(request.getRequestURI())
                        .ip(request.getRemoteAddr())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return eventService.findPublic(
                text, categories, paid,
                rangeStart, rangeEnd,
                onlyAvailable, sort, from, size
        );
}

    // 17 Получение подробной информации об опубликованном событии по его идентификатору
    @GetMapping("/{id}")
    public EventFullDto getPublicEvent(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        statsClient.saveHit(
                EndpointHitDto.builder()
                        .app("ewm-service")
                        .uri(request.getRequestURI())
                        .ip(request.getRemoteAddr())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return eventService.findPublicById(id);
    }
}
