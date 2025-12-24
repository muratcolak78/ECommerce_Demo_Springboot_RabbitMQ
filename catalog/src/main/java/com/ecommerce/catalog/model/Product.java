package com.ecommerce.catalog.model;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class Product {

    private Long id;
    private String name;
    private Category category;
    private BigDecimal price;
    private String imageUrl;
    private  String description;
    private String brand;
    private LocalDateTime createdAt;

}
