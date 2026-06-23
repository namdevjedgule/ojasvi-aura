package com.ojasvi.ecommerce.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    private Long id;

    private String productName;
    private String description;

    private String fabric;
    private String printType;

    private String collection;
    private String productType;

    private String color;
    private String size;

    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;

    private String dimensionUnit;

    private String careInstructions;
    private String tags;

    private Boolean isSet;
    private String setContents;

    private BigDecimal mrp;
    private BigDecimal sellingPrice;

    private Integer stock;

    private Boolean featured;
    private Boolean active;

    private Long categoryId;
    private Long subCategoryId;
}