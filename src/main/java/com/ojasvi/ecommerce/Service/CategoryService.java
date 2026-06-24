package com.ojasvi.ecommerce.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.DTO.CategoryCountProjection;
import com.ojasvi.ecommerce.DTO.CategoryShopDTO;
import com.ojasvi.ecommerce.DTO.SubCategoryDTO;
import com.ojasvi.ecommerce.Entity.Category;
import com.ojasvi.ecommerce.Repository.CategoryRepository;
import com.ojasvi.ecommerce.Repository.ProductRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public List<CategoryShopDTO> getCategoriesForShop() {

        List<Category> categories = categoryRepository.findAllWithSubCategories();

        Map<Long, Long> countMap = productRepository.getCategoryCounts()
                .stream()
                .collect(Collectors.toMap(
                        CategoryCountProjection::getCategoryId,
                        CategoryCountProjection::getProductCount
                ));

        return categories.stream().map(c -> {

            CategoryShopDTO dto = new CategoryShopDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setProductCount(countMap.getOrDefault(c.getId(), 0L));

            List<SubCategoryDTO> subs = c.getSubCategories()
                    .stream()
                    .map(s -> {
                        SubCategoryDTO sd = new SubCategoryDTO();
                        sd.setId(s.getId());
                        sd.setName(s.getName());
                        return sd;
                    }).toList();

            dto.setSubCategories(subs);

            return dto;
        }).toList();
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
