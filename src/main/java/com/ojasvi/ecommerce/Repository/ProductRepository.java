package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.SubCategory;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	 List<Product> findByIsActiveTrue();

	    List<Product> findByCategoryId(Long categoryId);

	    List<Product> findBySubCategoryId(Long subCategoryId);

	    Optional<Product> findBySlug(String slug);


    List<Product> findByFeaturedTrue();
    
    List<Product> findTop4BySubCategoryAndIdNotAndIsActiveTrue(
            SubCategory subCategory, Long id);
    
    List<Product> findBySubCategoryIdAndIsActiveTrue(Long subCategoryId);
    
    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

    long countByStockLessThanEqual(Integer stock);

}
