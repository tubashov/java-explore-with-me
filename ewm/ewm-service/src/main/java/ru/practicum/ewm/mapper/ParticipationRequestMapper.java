package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.ParticipationRequest;

@UtilityClass
public class ParticipationRequestMapper {

    public ParticipationRequestDto toDto(ParticipationRequest r) {
        return ParticipationRequestDto.builder()
                .id(r.getId())
                .event(r.getEvent().getId())
                .requester(r.getRequester().getId())
                .status(r.getStatus())
                .created(r.getCreated())
                .build();
    }
}
