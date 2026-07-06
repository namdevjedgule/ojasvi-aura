package com.ojasvi.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import com.ojasvi.ecommerce.Enum.PaymentMethod;
import com.ojasvi.ecommerce.Enum.PaymentStatus;


@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(unique = true)
    private String razorpayOrderId;

    @Column(unique = true)
    private String razorpayPaymentId;

    @Column(length = 500)
    private String razorpaySignature;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private String paymentGateway;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // Optional: UPI/Card/NetBanking/Wallet
    private String paymentMode;

    @Column(length = 1000)
    private String failureReason;
}
