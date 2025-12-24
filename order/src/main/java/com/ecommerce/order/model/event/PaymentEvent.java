package com.ecommerce.order.model.event;


import com.ecommerce.order.model.enums.EventStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentEvent {
    private Long orderId;
    private EventStatus status;
}
