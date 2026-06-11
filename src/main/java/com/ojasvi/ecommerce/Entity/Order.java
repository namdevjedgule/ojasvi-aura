package com.ojasvi.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;

import com.ojasvi.ecommerce.Enum.OrderStatus;
import com.ojasvi.ecommerce.Enum.PaymentStatus;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address shippingAddress;

    private Double subtotal;

    private Double shippingCharge;

    private Double discountAmount;

    private Double taxAmount;

    private Double grandTotal;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String remarks;
}
