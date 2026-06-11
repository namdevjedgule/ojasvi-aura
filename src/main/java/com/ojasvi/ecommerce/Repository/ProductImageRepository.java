package com.ojasvi.ecommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.ProductImage;

@Repository
public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long> {

}
