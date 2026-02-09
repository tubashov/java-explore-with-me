package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(" +
            "MIN(h.app), " +
            "h.uri, " +
            "COUNT(h.id)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.uri " +
            "ORDER BY COUNT(h.id) DESC")
    List<ViewStatsDto> findStats(LocalDateTime start,
                                 LocalDateTime end,
                                 List<String> uris);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(" +
            "MIN(h.app), " +
            "h.uri, " +
            "COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> findStatsUnique(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris);
}
