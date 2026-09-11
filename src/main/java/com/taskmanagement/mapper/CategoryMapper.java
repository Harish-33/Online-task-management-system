package com.taskmanagement.mapper;

import com.taskmanagement.dto.request.CategoryRequest;
import com.taskmanagement.dto.response.CategoryResponse;
import com.taskmanagement.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setColorCode(request.getColorCode());
        return category;
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getColorCode()
        );
    }
}
