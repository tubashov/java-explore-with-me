package ru.practicum.ewm.server.mapper;

import ru.practicum.ewm.server.model.EndpointHit;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

public class EndpointHitMapper {

    public static EndpointHit toEntity(EndpointHitDto dto) {
        if (dto == null) return null;

        return EndpointHit.builder()
                .id(dto.getId())
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public static EndpointHitDto toDto(EndpointHit entity) {
        if (entity == null) return null;

        return EndpointHitDto.builder()
                .id(entity.getId())
                .app(entity.getApp())
                .uri(entity.getUri())
                .ip(entity.getIp())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
