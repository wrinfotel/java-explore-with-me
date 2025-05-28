package ru.practicum.mapper;

import ru.practicum.dto.CategoryDto;
import ru.practicum.model.Category;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}
