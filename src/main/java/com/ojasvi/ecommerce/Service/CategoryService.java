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
    
    private String getUniqueCategoryCode(String code) {

        String finalCode = code;
        int counter = 1;

        while (categoryRepository.existsByCode(finalCode)) {

            finalCode = code + counter;
            counter++;
        }

        return finalCode;
    }
    
    private String generateCategoryCode(String name) {

        String code;

        String[] words = name.trim().split("\\s+");

        if (words.length == 1) {

            String cleaned = words[0]
                    .replaceAll("[^A-Za-z]", "")
                    .toUpperCase();

            code = cleaned.length() >= 3
                    ? cleaned.substring(0, 3)
                    : cleaned;
        } else {

            StringBuilder builder = new StringBuilder();

            for (String word : words) {
                builder.append(
                        Character.toUpperCase(word.charAt(0))
                );
            }

            code = builder.toString();
        }

        return getUniqueCategoryCode(code);
    }
    
    public Category save(Category category) {
    	
    	category.setCode(generateCategoryCode(category.getName()));
    	
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
