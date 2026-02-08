package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.ParticipationRequest;

public class ParticipationRequestMapper {

    public static ParticipationRequestDto toDto(ParticipationRequest r) {
        return ParticipationRequestDto.builder()
                .id(r.getId())
                .event(r.getEvent().getId())
                .requester(r.getRequester().getId())
                .status(r.getStatus())
                .created(r.getCreated())
                .build();
    }
}
