package com.ecommerce.order.kafka;

import com.ecommerce.events.inventory.InventoryEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryRemovalProducer {
    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    public InventoryRemovalProducer(KafkaTemplate<String, InventoryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendInventoryRemovalEvent(InventoryEvent event){
        kafkaTemplate.send("inventory_removal_event", event.getProductId().toString(), event);
    }
}
