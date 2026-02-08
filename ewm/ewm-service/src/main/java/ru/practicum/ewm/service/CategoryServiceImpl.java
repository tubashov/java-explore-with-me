package ru.practicum.ewm.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.UpdateCategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    // 3 Добавление новой категории
    @Override
    public CategoryDto create(NewCategoryDto dto) {
        log.info("Создание категории '{}'", dto.getName());

        Category category = Category.builder()
                .name(dto.getName())
                .build();

        try {
            return CategoryMapper.toDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Category name must be unique");
        }
    }

    // 4 Удаление категории
    @Override
    public void delete(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id=" + catId + " was not found");
        }

        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
    }

    // 5 Изменение категории
    @Override
    public CategoryDto update(Long catId, UpdateCategoryDto dto) {

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException("Category with id=" + catId + " was not found"));

        category.setName(dto.getName());

        try {
            return CategoryMapper.toDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Category name must be unique");
        }
    }

    // 12 Получение категорий
    @Override
    public List<CategoryDto> findAll(int from, int size) {

        Page<Category> page = categoryRepository.findAll(
                PageRequest.of(from / size, size, Sort.by("id").ascending())
        );

        log.debug("Found {} categories", page.getNumberOfElements());

        return page.getContent().stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    // 13 Получение информации о категории по её идентификатору
    @Override
    public CategoryDto findById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Category with id={} not found", catId);
                    return new NotFoundException(
                            String.format("Category with id=%d was not found", catId)
                    );
                });

        return ru.practicum.ewm.mapper.CategoryMapper.toDto(category);
    }
}
