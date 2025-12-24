package com.ecommerce.order.kafka;

import com.ecommerce.order.model.event.PaymentEvent;
import com.ecommerce.order.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {
    private final OrderService service;

    public PaymentEventListener(OrderService service) {
        this.service = service;
    }

    @KafkaListener(topics = "payment_event", groupId = "mygroup")
    public void paymentEventListener(PaymentEvent paymentEvent){
        service.updateStatus(paymentEvent);

    }
}
