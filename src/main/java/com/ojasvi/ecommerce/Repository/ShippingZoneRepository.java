package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.ShippingZone;

@Repository
public interface ShippingZoneRepository
        extends JpaRepository<ShippingZone, Long> {

	@Query("""
            SELECT z
            FROM ShippingZone z
            WHERE LOWER(z.regions)
            LIKE LOWER(CONCAT('%', :state, '%'))
            """)
    ShippingZone findByRegion(@Param("state")
                              String state);
	
	List<ShippingZone> findByIsActiveTrue();
	
	Optional<ShippingZone> findByZoneNameIgnoreCase(String zoneName);

}
