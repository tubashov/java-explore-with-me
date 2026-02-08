package ru.practicum.ewm.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Validated
public class PublicCompilationController {

    private final CompilationService compilationService;

    // 1 Получение подборок событий
    @GetMapping
    public List<CompilationDto> findAll(
            @RequestParam(required = false) Boolean pinned,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        return compilationService.findAll(pinned, from, size);
    }

    // 2 Получение подборки событий по его id
    @GetMapping("/{compId}")
    public CompilationDto findById(
            @PathVariable Long compId
    ) {
        return compilationService.findById(compId);
    }
}

