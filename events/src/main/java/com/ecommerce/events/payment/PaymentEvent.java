package com.ecommerce.events.payment;

import com.ecommerce.events.payment.EventStatus;
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
