package com.ojasvi.ecommerce.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ojasvi.ecommerce.Entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    boolean existsByCode(String code);
    
    Optional<Coupon> findByCodeIgnoreCase(String code);

    List<Coupon> findByDeletedFalseAndExpiryDateAfter(
            LocalDateTime dateTime);

    Optional<Coupon> findByCodeIgnoreCaseAndDeletedFalse(
            String code);

}
