package com.ojasvi.ecommerce.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ojasvi.ecommerce.Entity.AdminOtp;

@Repository
public interface AdminOtpRepository extends JpaRepository<AdminOtp, Long> {

    Optional<AdminOtp> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM AdminOtp o WHERE o.email = :email")
    void deleteAllByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM AdminOtp o WHERE o.expiresAt < CURRENT_TIMESTAMP")
    void deleteAllExpired();
}

