package com.ojasvi.ecommerce.DTO;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
public class CouponValidationResponse {

    private boolean valid;
    private String message;
    private BigDecimal discount;
}
