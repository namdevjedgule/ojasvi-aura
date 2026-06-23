package com.ojasvi.ecommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.ShippingZone;

@Repository
public interface ShippingZoneRepository
        extends JpaRepository<ShippingZone, Long> {

}
