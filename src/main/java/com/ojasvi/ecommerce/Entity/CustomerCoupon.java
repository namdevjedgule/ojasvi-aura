package com.ojasvi.ecommerce.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
	    name = "customer_coupons",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            columnNames = {"customer_id", "coupon_id"}
	        )
	    }
	)
@Getter
@Setter
public class CustomerCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private Boolean used = false;
    
    private LocalDateTime assignedDate;

    private LocalDateTime usedDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal savedAmount;
}
