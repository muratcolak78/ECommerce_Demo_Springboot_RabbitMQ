package com.ecommerce.order.kafka;

import com.ecommerce.order.model.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    @KafkaListener(topics = "order.created", groupId = "mygroup")
    public void onOrderSaved(OrderCreatedEvent event){
        System.out.println(event);
    }
}
