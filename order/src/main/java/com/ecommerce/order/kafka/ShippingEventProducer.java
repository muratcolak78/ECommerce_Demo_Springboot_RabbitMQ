package com.ecommerce.order.kafka;

import com.ecommerce.events.shipping.ShippingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShippingEventProducer {
    private final KafkaTemplate<String, ShippingEvent> kafkaTemplate;

    public ShippingEventProducer(KafkaTemplate<String, ShippingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendShippingEvent(ShippingEvent event){
        kafkaTemplate.send("shipping_event", event.getOrderId().toString(), event);
    }
}
