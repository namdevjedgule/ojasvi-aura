package com.ojasvi.ecommerce.DTO;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class WishlistDTO {
 
    private Long wishlistId;
    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price;
    private String slug;      
    private BigDecimal mrp;   
    private Integer stock;   
    private String category;   
}
