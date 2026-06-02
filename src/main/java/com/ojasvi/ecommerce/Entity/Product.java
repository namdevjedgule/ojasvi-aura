package com.ojasvi.ecommerce.Entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String slug;

    @Column(length = 5000)
    private String description;

    private String fabric;

    private String printType;

    private String sku;

    private Integer stock;

    private Boolean featured = false;

    private BigDecimal mrp;

    private BigDecimal sellingPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ProductImage> images;

}
