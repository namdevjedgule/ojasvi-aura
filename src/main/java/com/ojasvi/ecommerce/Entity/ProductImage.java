package com.ojasvi.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_images")
@Getter
@Setter
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private Boolean primaryImage = false;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
