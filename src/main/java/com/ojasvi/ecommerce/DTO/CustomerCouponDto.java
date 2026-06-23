package com.ojasvi.ecommerce.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ojasvi.ecommerce.Enum.DiscountType;

import lombok.*;

@Getter
@Setter
public class CustomerCouponDto {

    private String code;
    
    private String title;

    private String description;

    private DiscountType discountType;

    private BigDecimal discountValue;

    private BigDecimal minOrderAmount;

    private LocalDateTime expiryDate;

    private Long daysLeft;

    private LocalDateTime usedDate;
}
