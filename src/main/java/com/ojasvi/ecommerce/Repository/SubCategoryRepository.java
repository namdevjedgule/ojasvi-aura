package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

	List<SubCategory> findByCategoryIdAndIsActiveTrue(Long categoryId);

	List<SubCategory> findByIsActiveTrue();

	List<SubCategory> findByCategoryId(Long categoryId);

    Optional<SubCategory> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByCode(String code);

}
