package com.ojasvi.ecommerce.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.ProductImage;

@Repository
public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long> {
	
	List<ProductImage> findByProductId(Long productId);

	List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

}
