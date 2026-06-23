package com.ojasvi.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipping_zone")
@Getter
@Setter
public class ShippingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String zoneName;

    @Column(length = 1000)
    private String regions;

    private Double standardCharge;

    private Double expressCharge;

    private Boolean isActive = true;
}
