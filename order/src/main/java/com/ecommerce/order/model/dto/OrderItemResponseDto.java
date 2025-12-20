package com.ecommerce.order.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class OrderItemResponseDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal priceSnapshot;
    private BigDecimal lineTotal;

}
