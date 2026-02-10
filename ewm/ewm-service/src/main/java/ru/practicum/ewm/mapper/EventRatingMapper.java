package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.event.EventRatingDto;
import ru.practicum.ewm.model.EventRating;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

@UtilityClass
public class EventRatingMapper {

    // Entity to DTO
    public EventRatingDto toDto(EventRating rating) {
        if (rating == null) return null;

        return EventRatingDto.builder()
                .eventId(rating.getEvent().getId()) // ID из объекта Event
                .userId(rating.getUser().getId())   // ID из объекта User
                .liked(rating.getLiked())
                .build();
    }

    // DTO to Entity
    // Нужно передать объект Event и User, так как в Entity хранятся сущности
    public EventRating toEntity(EventRatingDto dto, Event event, User user) {
        if (dto == null) return null;

        return EventRating.builder()
                .event(event)
                .user(user)
                .liked(dto.getLiked())
                .build();
    }
}
