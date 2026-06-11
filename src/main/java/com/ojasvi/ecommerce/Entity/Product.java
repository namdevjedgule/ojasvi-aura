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

    private String productName;

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
    
    private String metaTitle;

    private String metaDescription;

    private Double weight;

    private String color;

    private String size;

    private Integer soldCount = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ProductImage> images;

}
