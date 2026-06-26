package com.ojasvi.ecommerce.DTO;

import java.time.LocalDate;

import com.ojasvi.ecommerce.Enum.OrderStatus;

import lombok.Data;

@Data
public class TrackingResponse {

    private String orderNumber;

    private OrderStatus orderStatus;

    private String trackingNumber;

    private String courierName;

    private LocalDate shippedDate;

    private LocalDate estimatedDeliveryDate;

    private LocalDate deliveredDate;

}
