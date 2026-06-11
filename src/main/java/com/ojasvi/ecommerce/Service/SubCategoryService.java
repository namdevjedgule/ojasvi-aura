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
	
	public SubCategory save(SubCategory subCategory) {
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

}
