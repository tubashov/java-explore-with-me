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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stats.server.url}")
    private String serverUrl;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // Отправка события о посещении на сервер статистики
    public void saveHit(EndpointHitDto hit) {
        log.info("Sending hit to stats server: {}", hit);
        try {
            restTemplate.postForEntity(serverUrl + "/hit", hit, Void.class);
        } catch (Exception e) {
            log.error("Failed to send hit: {}", e.getMessage());
        }
    }

    // Получение статистики за заданный период для заданных URIs
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       boolean unique,
                                       List<String> uris) {

        var uriBuilder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("unique", unique);

        // Добавляем параметры URIs, если они есть
        if (uris != null && !uris.isEmpty()) {
            uris.forEach(u -> uriBuilder.queryParam("uris", u));
        }

        String url = uriBuilder.toUriString();
        log.info("Requesting stats from server: {}", url);

        try {
            ResponseEntity<ViewStatsDto[]> response =
                    restTemplate.getForEntity(url, ViewStatsDto[].class);

            // Проверка, что статистика пришла
            if (response.getBody() != null) {
                log.info("Received stats, count={}", response.getBody().length);
                return List.of(response.getBody());
            } else {
                log.warn("No stats received.");
                return List.of();
            }
        } catch (Exception e) {
            log.error("Failed to fetch stats: {}", e.getMessage());
            return List.of(); // Возвращаем пустой список в случае ошибки
        }
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
        }
    }
}
