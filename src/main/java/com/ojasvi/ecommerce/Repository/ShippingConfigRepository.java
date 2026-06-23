package com.ojasvi.ecommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.ShippingConfig;

@Repository
public interface ShippingConfigRepository
        extends JpaRepository<ShippingConfig, Long> {

}
