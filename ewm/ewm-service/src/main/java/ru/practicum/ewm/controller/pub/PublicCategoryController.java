package ru.practicum.ewm.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCategoryController {

    private final CategoryService categoryService;

    // 12 Получение информации о категории по её идентификатору
    @GetMapping
    public List<CategoryDto> getCategories(
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("GET /categories from={} size={}", from, size);
        return categoryService.findAll(from, size);
    }

    // 13 Получение информации о категории по её идентификатору
    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(
            @PathVariable Long catId
    ) {
        log.debug("GET /categories/{}", catId);
        return categoryService.findById(catId);
    }
}
