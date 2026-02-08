package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate restTemplate;

    @Value("${stats.server.url}")
    private String serverUrl;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Отправка события о посещении на сервер статистики
    public void saveHit(EndpointHitDto hit) {
        log.info("Sending hit to stats server: {}", hit);
        try {
            restTemplate.postForEntity(serverUrl + "/hit", hit, Void.class);
        } catch (Exception e) {
            log.error("Failed to send hit: {}", e.getMessage());
            throw new RuntimeException("Stats server is unavailable", e);
        }
    }

    // Получение статистики за заданный период для заданных URIs
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       boolean unique,
                                       List<String> uris) {

        String startStr = URLEncoder.encode(start.format(FORMATTER), StandardCharsets.UTF_8);
        String endStr = URLEncoder.encode(end.format(FORMATTER), StandardCharsets.UTF_8);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", startStr)
                .queryParam("end", endStr)
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            uris.forEach(u -> builder.queryParam("uris", u));
        }

        String url = builder.build(false).toUriString(); // теперь даты закодированы
        log.info("Requesting stats from server: {}", url);

        ResponseEntity<ViewStatsDto[]> response =
                restTemplate.getForEntity(url, ViewStatsDto[].class);

        if (response.getBody() == null) {
            return List.of();
        }
        return List.of(response.getBody());
    }

    // Дополнительный метод для обновления статистики при просмотре событий
    public void updateEventStats(String appName, Long eventId, String ip) {
        try {
            // Создание объекта EndpointHitDto с использованием сеттеров
            EndpointHitDto hit = new EndpointHitDto();
            hit.setApp(appName);
            hit.setUri("/events/" + eventId);
            hit.setTimestamp(LocalDateTime.now());
            hit.setIp(ip); // предполагаем, что IP передается в метод

            // Отправка статистики для конкретного события
            saveHit(hit);
            log.info("Updated stats for event: {}", eventId);
        } catch (Exception e) {
            log.error("Error while updating stats for event {}: {}", eventId, e.getMessage());
            throw new RuntimeException("Stats server is unavailable", e);
        }
    }
}
