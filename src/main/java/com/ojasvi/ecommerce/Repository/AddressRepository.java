package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ojasvi.ecommerce.Entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserIdOrderByDefaultAddressDescCreatedAtDesc(Long userId);
    
    List<Address> findByUserId(Long userId);
    
    Optional<Address> findByUserIdAndDefaultAddressTrue(Long userId);

}
