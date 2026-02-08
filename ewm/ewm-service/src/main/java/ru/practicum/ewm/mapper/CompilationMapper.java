package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.event.CompilationDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.EventState;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

import java.util.List;

@UtilityClass
public class CompilationMapper {

    // Compilation -> CompilationDto
    public CompilationDto toDto(Compilation compilation) {
        if (compilation == null) return null;

        List<EventShortDto> eventDtos = compilation.getEvents() != null && !compilation.getEvents().isEmpty()
                ? compilation.getEvents().stream()
                .filter(e -> e.getState() == EventState.PUBLISHED) // только опубликованные события
                .map(EventMapper::toShortDto)
                .toList()
                : List.of(); // пустой список вместо null

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventDtos)
                .build();
    }

    // DTO -> Entity
    public Compilation toEntity(CompilationDto dto, List<Event> events) {
        if (dto == null) return null;

        return Compilation.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .pinned(dto.getPinned() != null ? dto.getPinned() : false)
                .events(events != null ? events : List.of())
                .build();
    }
}
