package com.ecommerce.order.kafka;

import com.ecommerce.order.model.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publishOrderSaved(OrderCreatedEvent event){
        kafkaTemplate.send("order.created", event.getOrderId().toString(),event);
    }
}
