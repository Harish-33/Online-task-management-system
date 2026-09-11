package com.taskmanagement.service;

import com.taskmanagement.dto.request.CategoryRequest;
import com.taskmanagement.dto.response.CategoryResponse;
import com.taskmanagement.entity.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    Category getCategoryEntityById(Long id);
}
