package com.ecommerce.payment.model.event;

import com.ecommerce.payment.model.enums.EventStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentEvent {
    private Long orderId;
    private EventStatus status;
}
