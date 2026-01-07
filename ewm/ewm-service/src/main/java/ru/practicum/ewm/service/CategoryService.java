package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.CategoryUpdateRequest;
import ru.practicum.ewm.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto dto);

    CategoryDto update(Long catId, CategoryUpdateRequest request);

    List<CategoryDto> findAll(int from, int size);

    CategoryDto findById(Long catId);

    void delete(Long id);
}
