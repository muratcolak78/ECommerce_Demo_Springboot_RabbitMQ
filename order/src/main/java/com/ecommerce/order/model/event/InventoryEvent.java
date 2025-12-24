package com.ecommerce.order.model.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryEvent {
    private Long productId;
    private Integer amount;
}
