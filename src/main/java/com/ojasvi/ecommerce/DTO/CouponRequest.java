package com.ojasvi.ecommerce.DTO;

import java.util.List;

import com.ojasvi.ecommerce.Entity.Coupon;
import com.ojasvi.ecommerce.Enum.CouponAudience;

import lombok.*;

@Getter
@Setter
public class CouponRequest {

    private Long id;

    private String code;

    private String title;

    private CouponAudience audience;
    
    private Coupon coupon;

    private List<Long> customerIds;

}
