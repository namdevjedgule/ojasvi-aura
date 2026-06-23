package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.CustomerCoupon;

@Repository
public interface CustomerCouponRepository extends JpaRepository<CustomerCoupon, Long> {

    List<CustomerCoupon> findByCustomerIdAndUsedFalse(Long customerId);

    List<CustomerCoupon> findByCustomerIdAndUsedTrue(Long customerId);

    Long countByCustomerIdAndUsedFalse(Long customerId);

    Long countByCustomerIdAndUsedTrue(Long customerId);

    Optional<CustomerCoupon> findByCustomerIdAndCouponCodeIgnoreCase(
            Long customerId,
            String code
    );
}
