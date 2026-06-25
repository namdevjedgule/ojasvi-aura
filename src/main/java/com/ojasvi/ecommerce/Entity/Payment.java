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
    @JoinColumn(name = "order_id")
    private Order order;

    private String transactionId;

    private BigDecimal amount;

    private String paymentGateway;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
