package com.ojasvi.ecommerce.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    boolean existsByRazorpayPaymentId(String razorpayPaymentId);

}