package com.ecommerce.order.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {
    private DeliveryAddress deliveryAddress;
}
