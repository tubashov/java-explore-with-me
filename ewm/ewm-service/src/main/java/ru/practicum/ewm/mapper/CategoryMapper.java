package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.model.Category;

public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static Category toEntity(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
