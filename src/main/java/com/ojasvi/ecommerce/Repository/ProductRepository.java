package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.DTO.CategoryCountProjection;
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.SubCategory;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);

    List<Product> findByIsActiveTrue();

    List<Product> findByFeaturedTrue();

    List<Product> findByFeaturedTrueAndIsActiveTrue();

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

    List<Product> findBySubCategoryId(Long subCategoryId);

    List<Product> findBySubCategoryIdAndIsActiveTrue(Long subCategoryId);

    List<Product> findTop4BySubCategoryAndIdNotAndIsActiveTrue(SubCategory subCategory, Long productId);

    long countByIsActiveTrue();

    long countByStockLessThanEqual(Integer stock);

    long countByStockLessThanEqualAndIsActiveTrue(Integer stock);

    List<Product> findTop8ByIsActiveTrueOrderBySoldCountDesc();

    List<Product> findTop12ByFeaturedTrueAndIsActiveTrue();

    List<Product> findTop12ByIsActiveTrueOrderByCreatedAtDesc();

    long countBySubCategoryId(Long subCategoryId);

    Long countByCategoryIdAndIsActiveTrue(Long categoryId);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    long countByStockGreaterThan(Integer stock);

    long countByStockBetween(Integer min, Integer max);

    long countByStock(Integer stock);

    List<Product> findByStockBetween(Integer min, Integer max);

    List<Product> findByStock(Integer stock);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.category LEFT JOIN FETCH p.subCategory")
    List<Product> findAllWithImages();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.category LEFT JOIN FETCH p.subCategory WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            LEFT JOIN FETCH p.images
            WHERE p.isActive = true
            ORDER BY p.createdAt DESC
        """)
    List<Product> findAllActiveWithImages();

    @Query("""
            SELECT
                p.category.id as categoryId,
                COUNT(p.id) as productCount
            FROM Product p
            WHERE p.isActive = true
            GROUP BY p.category.id
            """)
    List<CategoryCountProjection> getCategoryCounts();

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.subCategory
            LEFT JOIN FETCH p.images
            WHERE p.isActive = true
            ORDER BY p.createdAt DESC
            """)
    List<Product> findAllActiveProductsWithRelations();

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            LEFT JOIN FETCH p.images
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.subCategory
            WHERE p.isActive = true
        """)
    List<Product> findActiveProductsWithImages();

    @Query("""
            SELECT p FROM Product p
            LEFT JOIN FETCH p.images
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.subCategory
            WHERE p.slug = :slug
        """)
    Optional<Product> findBySlugWithImages(@Param("slug") String slug);

    @Query("""
            SELECT DISTINCT p FROM Product p
            LEFT JOIN FETCH p.images
            WHERE p.subCategory = :subCategory
              AND p.id <> :excludeId
              AND p.isActive = true
        """)
    List<Product> findRelatedWithImages(
            @Param("subCategory") SubCategory subCategory,
            @Param("excludeId") Long excludeId,
            Pageable pageable);
}