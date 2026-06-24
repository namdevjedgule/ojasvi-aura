package com.ojasvi.ecommerce.DTO;
import java.util.List;
import lombok.*;

@Getter
@Setter
public class CategoryShopDTO {

    private Long id;
    private String name;
    private Long productCount;
    private List<SubCategoryDTO> subCategories;
}
