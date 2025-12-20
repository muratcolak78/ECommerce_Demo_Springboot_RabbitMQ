package com.ecommerce.order.model;

import com.ecommerce.order.model.enums.Status;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private Status status;
    private BigDecimal totalAmount;
}
