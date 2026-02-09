package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    // 24 Добавление новой подборки (подборка может не содержать событий)
    @Transactional
    public CompilationDto create(NewCompilationDto dto) {
        log.info("Creating new compilation with title='{}'", dto.getTitle());

        // Получаем события по id, если переданы
        List<Event> events = dto.getEvents() != null && !dto.getEvents().isEmpty()
                ? eventRepository.findAllById(dto.getEvents())
                : List.of();

        Boolean pinned = dto.getPinned() != null ? dto.getPinned() : false;

        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(pinned)
                .events(events)
                .build();

        Compilation saved = compilationRepository.save(compilation);

        log.info("Compilation created successfully with id={}", saved.getId());

        List<EventShortDto> eventDto = saved.getEvents() != null && !saved.getEvents().isEmpty()
                ? saved.getEvents().stream()
                .map(EventMapper::toShortDto)
                .toList()
                : List.of();

        return CompilationDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .pinned(saved.getPinned())
                .events(eventDto)
                .build();
    }

    // 26 Обновить информацию о подборке
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationDto dto) {
        log.info("Updating compilation with id={}", compId);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        "Compilation with id=" + compId + " was not found"
                ));

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            List<Event> events = dto.getEvents().isEmpty() ? new ArrayList<>() :
                    eventRepository.findAllById(dto.getEvents());
            compilation.setEvents(events);
        }

        Compilation updated = compilationRepository.save(compilation);

        log.info("Compilation with id={} updated successfully", updated.getId());

        return CompilationMapper.toDto(updated);
    }

    // 25 Удаление подборки
    @Transactional
    public void delete(Long compId) {
        log.info("Deleting compilation with id={}", compId);

        if (!compilationRepository.existsById(compId)) {
            log.warn("Compilation with id={} not found", compId);
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }

        compilationRepository.deleteById(compId);

        log.info("Compilation with id={} deleted successfully", compId);
    }
}
