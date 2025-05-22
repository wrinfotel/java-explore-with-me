package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        return null;
    }

    @Override
    public void deleteCategory(Long catId) {

    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long catId) {
        return null;
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return List.of();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return null;
    }
}
