package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, count(h)) " +
            "from EndpointHit h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri " +
            "order by count(h) desc")
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<ViewStatsDto> getUniqueStats(LocalDateTime start, LocalDateTime end);
}
