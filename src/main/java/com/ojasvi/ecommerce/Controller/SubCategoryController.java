package com.ojasvi.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojasvi.ecommerce.Entity.SubCategory;
import com.ojasvi.ecommerce.Service.SubCategoryService;

@RestController
@RequestMapping("/api/subcategories")
@CrossOrigin("*")
public class SubCategoryController {

    @Autowired
    private SubCategoryService subCategoryService;

    @PostMapping
    public SubCategory saveSubCategory(
            @RequestBody SubCategory subCategory) {

        return subCategoryService.save(subCategory);
    }

    @PutMapping("/{id}")
    public SubCategory updateSubCategory(
            @PathVariable Long id,
            @RequestBody SubCategory subCategory) {

        return subCategoryService.update(id, subCategory);
    }
}
