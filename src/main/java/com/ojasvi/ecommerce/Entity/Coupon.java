package com.ojasvi.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ojasvi.ecommerce.Enum.CouponApplyType;
import com.ojasvi.ecommerce.Enum.CouponAudience;
import com.ojasvi.ecommerce.Enum.CustomerType;
import com.ojasvi.ecommerce.Enum.DiscountType;

@Entity
@Table(name = "coupons")
@Getter
@Setter
public class Coupon extends BaseEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(precision = 12, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal minOrderAmount;

    private Integer maxUses;

    private Integer usedCount = 0;

    private Integer maxUsesPerCustomer;

    private Boolean firstOrderOnly = false;
    
    private Boolean autoApply = false;
    
    private Boolean freeShipping = false;

    private LocalDateTime startDate;

    private LocalDateTime expiryDate;
    
    @Enumerated(EnumType.STRING)
    private CouponApplyType applyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(length = 10)
    private String countryCode;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;
    
    @Column(length = 10)
    private String currencyCode;

    private Integer priority = 1;

    @Column(length = 255)
    private String bannerImage;

    @Column(length = 1000)
    private String adminNotes;
    
    private Boolean deleted = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponAudience audience = CouponAudience.GLOBAL;
}
