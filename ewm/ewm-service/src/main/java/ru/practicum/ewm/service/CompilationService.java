package ru.practicum.ewm.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.event.CompilationDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository  ;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    // 1 Получение подборок событий
    public List<CompilationDto> findAll(
            Boolean pinned,
            int from,
            int size
    ) {
        if (size <= 0) {
            throw new BadRequestException("Size must be greater than 0");
        }

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Compilation> page;

        if (pinned != null) {
            page = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            page = compilationRepository.findAll(pageable);
        }

        return page.getContent().stream()
                .map(CompilationMapper::toDto)
                .toList();
    }

    // 2 Получение подборки событий по его id
    public CompilationDto findById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() ->
                        new NotFoundException("Compilation with id=" + compId + " was not found")
                );

        return CompilationMapper.toDto(compilation);
    }
}
