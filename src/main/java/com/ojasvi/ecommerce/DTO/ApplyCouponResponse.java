package com.ojasvi.ecommerce.DTO;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
public class ApplyCouponResponse {

    private String couponCode;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private String message;
}
