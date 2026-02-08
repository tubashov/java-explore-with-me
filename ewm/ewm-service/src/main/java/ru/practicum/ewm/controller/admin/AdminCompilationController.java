package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.CompilationDto;
import ru.practicum.ewm.dto.event.NewCompilationDto;
import ru.practicum.ewm.dto.event.UpdateCompilationDto;
import ru.practicum.ewm.service.AdminCompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController {

    private final AdminCompilationService compilationService;

    // 24 Добавление новой подборки (подборка может не содержать событий)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto dto) {
        return compilationService.create(dto);
    }

    // 25 Удаление подборки
    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long compId) {
        compilationService.delete(compId);
    }

    // 26 Обновить информацию о подборке
    @PatchMapping("/{compId}")
    public CompilationDto update(
            @Positive @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationDto dto
    ) {
        return compilationService.update(compId, dto);
    }
}
