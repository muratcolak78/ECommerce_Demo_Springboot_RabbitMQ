package com.ecommerce.product.model.eventmodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductEvent {
    private Long productId;
    private Integer stock;
}
