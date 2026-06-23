package com.ojasvi.ecommerce.Entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
	    name = "products",
	    indexes = {
	        @Index(name = "idx_product_slug", columnList = "slug"),
	        @Index(name = "idx_product_category", columnList = "category_id"),
	        @Index(name = "idx_product_subcategory", columnList = "sub_category_id")
	    }
	)
@Getter
@Setter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String productName;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(length = 5000)
    private String description;

    private String fabric;

    private String printType;

    private String sku;

    @Column(nullable = false)
    private Integer stock = 0;

    private Boolean featured = false;

    @Column(nullable = false)
    private BigDecimal mrp;

    @Column(nullable = false)
    private BigDecimal sellingPrice;
    
    private String metaTitle;

    private String metaDescription;

    private String color;
    
    private String size;

    private Integer soldCount = 0;
    
    private String collection;
    
    private String productType;
    
    private Boolean isSet = false;

    @Column(length = 500)
    private String setContents;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(precision = 10, scale = 2)
    private BigDecimal length;

    @Column(precision = 10, scale = 2)
    private BigDecimal width;

    private String dimensionUnit;
    
    @Column(length = 1000)
    private String careInstructions;
    
    private String tags;

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
