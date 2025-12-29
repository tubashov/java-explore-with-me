package ru.practicum.ewm.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.server.mapper.EndpointHitMapper;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.server.model.EndpointHit;
import ru.practicum.ewm.server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository repository;
    private EndpointHitMapper endpointHitMapper;

    @Override
    public EndpointHitDto save(EndpointHitDto dto) {

        EndpointHit entity = EndpointHitMapper.toEntity(dto);
        EndpointHit saved = repository.save(entity);
        return EndpointHitMapper.toDto(saved);
    }



    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, Boolean unique) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and End must not be null");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        if (start.equals(end)) {
            throw new IllegalArgumentException("Start and end date must not be equal");
        }

        return unique
                ? repository.getUniqueStats(start, end)
                : repository.getStats(start, end);
    }
}
