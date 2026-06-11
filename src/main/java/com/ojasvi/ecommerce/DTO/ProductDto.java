package com.ojasvi.ecommerce.DTO;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
public class ProductDto {

    private String name;
    private String slug;
    private String description;
    private String fabric;
    private String printType;
    private String sku;
    private Integer stock;
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private Long categoryId;
}
