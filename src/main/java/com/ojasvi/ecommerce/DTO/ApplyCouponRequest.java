package com.ojasvi.ecommerce.DTO;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
public class ApplyCouponRequest {

    private String couponCode;
    private BigDecimal cartTotal;
}
