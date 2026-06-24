package com.ojasvi.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;

    private String mobile;
    
    private String email;

    private String addressLine1;

    private String addressLine2;
    
    private String landmark;

    private String city;

    private String state;

    private String country;

    private String pincode;

    private Boolean defaultAddress = false;
}
