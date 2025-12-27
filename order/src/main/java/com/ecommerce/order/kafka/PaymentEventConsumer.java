package com.ecommerce.order.kafka;

import com.ecommerce.events.payment.PaymentEvent;
import com.ecommerce.order.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {
    private final OrderService service;

    public PaymentEventConsumer(OrderService service) {
        this.service = service;
    }

    @KafkaListener(topics = "payment_event")
    public void paymentEventListener(PaymentEvent paymentEvent){
        service.updateStatus(paymentEvent);

    }
}
