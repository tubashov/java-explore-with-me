package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.UpdateCategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;

import java.util.List;

public interface    CategoryService {

    // 3 Добавление новой категории
    CategoryDto create(NewCategoryDto dto);

    // 4 Удаление категории
    void delete(Long catId);

    // 5 Изменение категории
    CategoryDto update(Long catId, UpdateCategoryDto dto);

    // 12 Получение категорий
    List<CategoryDto> findAll(int from, int size);

    // 13 Получение информации о категории по её идентификатору
    CategoryDto findById(Long catId);
}
