package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.Location;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.model.*;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .eventDate(event.getEventDate())
                .location(toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .confirmedRequests(event.getConfirmedRequests())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .views(event.getViews())
                .build();
    }

    public EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .build();
    }

    public Event toEntity(NewEventDto dto, User initiator) {
        Event e = new Event();

        e.setTitle(dto.getTitle());
        e.setAnnotation(dto.getAnnotation());
        e.setDescription(dto.getDescription());
        e.setEventDate(dto.getEventDate());
        e.setPaid(dto.getPaid());
        e.setParticipantLimit(dto.getParticipantLimit());
        e.setRequestModeration(dto.getRequestModeration());
        e.setInitiator(initiator);
        e.setLocation(toLocation(dto.getLocation()));
        e.setState(EventState.PENDING);
        e.setCreatedOn(LocalDateTime.now());

        return e;
    }

    public void updateEvent(Event event, UpdateEventUserRequest dto) {

        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getLocation() != null) event.setLocation(toLocation(dto.getLocation()));
    }

    private EventLocation toLocation(Location dto) {
        if (dto == null) return null;

        EventLocation loc = new EventLocation();
        loc.setLat(dto.getLat());
        loc.setLon(dto.getLon());
        return loc;
    }

    private Location toLocationDto(EventLocation loc) {
        if (loc == null) return null;

        return new Location(loc.getLat(), loc.getLon());
    }

    public void updateEventByAdmin(Event event, UpdateEventAdminRequest dto) {

        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getLocation() != null) event.setLocation(toLocation(dto.getLocation()));
    }
}
