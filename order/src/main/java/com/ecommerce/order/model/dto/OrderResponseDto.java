package com.ecommerce.order.model.dto;

import com.ecommerce.order.model.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderResponseDto {
    private Long orderId;
    private Status status;
    private BigDecimal totalAmount;
    private List<OrderItemResponseDto> items;
}
