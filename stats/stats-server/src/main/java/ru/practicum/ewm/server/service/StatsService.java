package ru.practicum.ewm.server.service;

import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHit save(EndpointHit hit);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, Boolean unique);
}
