package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    List<Wishlist> findByUserId(Long userId);
    
    @Query("""
            SELECT DISTINCT w FROM Wishlist w
            LEFT JOIN FETCH w.product p
            LEFT JOIN FETCH p.images
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.subCategory
            WHERE w.user.id = :userId
        """)
    List<Wishlist> findByUserIdWithProductDetails(@Param("userId") Long userId);
    
    long countByUserId(Long userId);
}
