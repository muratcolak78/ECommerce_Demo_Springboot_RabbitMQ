package com.ecommerce.shipping.kafka;



import com.ecommerce.events.shipping.ShippingEvent;
import com.ecommerce.shipping.service.ShippingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShippingrEventConsumer {
    private final ShippingService service;

    public ShippingrEventConsumer(ShippingService service) {
        this.service = service;
    }

    @KafkaListener(topics = "shipping_event")
    public void orderEventListener(ShippingEvent event){
        service.getShippingEvent(event);

    }
}
