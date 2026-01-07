package ru.practicum.ewm.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.CategoryUpdateRequest;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public CategoryDto create(NewCategoryDto dto) {
        log.info("Создание категории '{}'", dto.getName());

        Category category = Category.builder()
                .name(dto.getName())
                .build();

        try {
            return toDto(repository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Category name must be unique");
        }
    }

    @Override
    public CategoryDto update(Long catId, CategoryUpdateRequest request) {

        Category category = repository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException("Category with id=" + catId + " was not found"));

        category.setName(request.getName());

        try {
            return toDto(repository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Category name must be unique");
        }
    }

    @Override
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }

        try {
            repository.deleteById(id);
            log.info("Категория {} удалена", id);
        } catch (DataIntegrityViolationException e) {
            // когда с категорией связаны события
            throw new ConflictException("The category is not empty");
        }
    }

    @Override
    public List<CategoryDto> findAll(int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);

        return repository.findAll(page)
                .map(this::toDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long catId) {
        return repository.findById(catId)
                .map(this::toDto)
                .orElseThrow(() ->
                        new NotFoundException("Category with id=" + catId + " was not found"));
    }

    private CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
