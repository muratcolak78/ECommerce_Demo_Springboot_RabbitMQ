package com.ecommerce.payment.kafka;


import com.ecommerce.payment.model.event.PaymentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void paymentEventSend(PaymentEvent event){
        kafkaTemplate.send("payment_event", event.getOrderId().toString(), event);

    }
}
