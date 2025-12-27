package com.ecommerce.order.kafka;

import com.ecommerce.events.inventory.InventoryEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryReservedProducer {
    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    public InventoryReservedProducer(KafkaTemplate<String, InventoryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendInventoryEvent(InventoryEvent event){
        kafkaTemplate.send("inventory_reserved_event",event.getProductId().toString(), event);
    }
}
