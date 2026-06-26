package com.ojasvi.ecommerce.DTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailsDto {

    private Long id;

    private String orderNumber;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String createdAt;

    private String addressLine1;

    private String addressLine2;

    private String landmark;

    private String city;

    private String state;

    private String country;

    private String pincode;

    private BigDecimal subtotal;

    private BigDecimal shippingCharge;

    private BigDecimal discountAmount;

    private BigDecimal taxAmount;

    private BigDecimal grandTotal;

    private String paymentMethod;

    private String paymentStatus;

    private String orderStatus;

    private String remarks;

    private List<OrderItemDto> items = new ArrayList<>();

}
