package ru.practicum.ewm.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.StatsRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.server.service.StatsService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto save(@RequestBody @Valid EndpointHitDto hitDto) {
        log.info("Received new hit: {}", hitDto);
        return service.save(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@Valid @ModelAttribute StatsRequestDto request) {
        log.info("Request stats from {} to {}, unique={}", request.getStart(), request.getEnd(), request.getUnique());
        return service.getStats(request.getStart(), request.getEnd(), request.getUnique());
    }

}
