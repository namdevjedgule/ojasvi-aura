package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.SubCategory;
import com.ojasvi.ecommerce.Repository.SubCategoryRepository;

@Service
public class SubCategoryService {

	@Autowired
    private SubCategoryRepository subCategoryRepository;

	public List<SubCategory> getByCategoryId(Long categoryId) {
	    return subCategoryRepository.findByCategoryIdAndIsActiveTrue(categoryId);
	}

	public Object findAll() {
		return subCategoryRepository.findAll();
	}
	
	private String getUniqueSubCategoryCode(String code) {

	    String finalCode = code;
	    int counter = 1;

	    while(subCategoryRepository.existsByCode(finalCode)) {

	        finalCode = code + counter;
	        counter++;
	    }

	    return finalCode;
	}
	
	private String generateSubCategoryCode(String name) {

	    String code;

	    String[] words = name.trim().split("\\s+");

	    if(words.length == 1) {

	        String word = words[0]
	                .replaceAll("[^A-Za-z]", "")
	                .toUpperCase();

	        code = word.length() >= 3
	                ? word.substring(0, 3)
	                : word;
	    } else {

	        StringBuilder builder = new StringBuilder();

	        for(String word : words) {

	            String clean = word.replaceAll("[^A-Za-z]", "")
	                    .toUpperCase();

	            if(!clean.isEmpty()) {
	                builder.append(clean.charAt(0));
	            }
	        }

	        code = builder.toString();
	    }

	    return getUniqueSubCategoryCode(code);
	}
	
	public SubCategory save(SubCategory subCategory) {
		
		subCategory.setCode(
	            generateSubCategoryCode(
	                    subCategory.getName()
	            )
	    );
		
	    return subCategoryRepository.save(subCategory);
	}

	public SubCategory update(Long id, SubCategory subCategory) {

    SubCategory existing = subCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sub Category not found"));

    existing.setName(subCategory.getName());
    existing.setSlug(subCategory.getSlug());
    existing.setIsActive(subCategory.getIsActive());
    existing.setCategory(subCategory.getCategory());

    return subCategoryRepository.save(existing);
}

	public List<SubCategory> getAllSubCategories() {
		return subCategoryRepository.findAll();
	}

	public List<SubCategory> findAllActive() {
	    return subCategoryRepository
	            .findByIsActiveTrue();
	}
}
