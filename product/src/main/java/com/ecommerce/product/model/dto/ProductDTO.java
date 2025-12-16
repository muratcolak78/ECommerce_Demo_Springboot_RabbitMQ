package com.ecommerce.product.model.dto;

import com.ecommerce.product.model.Category;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {

    private Long id;

    private String name;

    private Category category;

    private BigDecimal price;

    private String imageUrl;

    private Integer stock;


}
