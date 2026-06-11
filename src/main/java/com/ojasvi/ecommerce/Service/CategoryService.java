package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.Category;
import com.ojasvi.ecommerce.Repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category categoryRequest) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        category.setName(categoryRequest.getName());
        category.setSlug(categoryRequest.getSlug());
        category.setIsActive(categoryRequest.getIsActive());

        return categoryRepository.save(category);
    }
}
