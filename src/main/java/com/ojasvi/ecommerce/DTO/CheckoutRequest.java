package com.ojasvi.ecommerce.DTO;

import lombok.*;

@Getter
@Setter
public class CheckoutRequest {
    private Long addressId;
    private String paymentMethod; // COD, ONLINE
}
