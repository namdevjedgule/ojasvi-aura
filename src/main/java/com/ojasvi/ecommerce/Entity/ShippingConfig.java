package com.ojasvi.ecommerce.Entity;

import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipping_config")
@Getter
@Setter
public class ShippingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Standard Shipping
    private Boolean standardEnabled = true;
    private Double standardCharge = 99.0;
    private Double standardFreeAbove = 999.0;

    // Express Shipping
    private Boolean expressEnabled = false;
    private Double expressCharge = 299.0;
    private Double expressFreeAbove = 2999.0;

    // COD
    private Boolean codEnabled = true;
    private Double codCharge = 50.0;
    private Double codMaxOrder = 5000.0;

    // General
    private Double freeShippingThreshold = 999.0;
    private String deliveryMessage = "Delivered in 5–7 business days";
    private LocalTime cutoffTime = LocalTime.of(14, 0);
    
    private Boolean shippingEnabled;

    private Boolean internationalShipping;

    private Double weightBasedRate;

    private Double handlingCharge;
}
