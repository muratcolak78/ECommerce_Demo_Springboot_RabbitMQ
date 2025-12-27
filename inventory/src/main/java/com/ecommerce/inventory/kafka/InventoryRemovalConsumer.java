package com.ecommerce.inventory.kafka;

import com.ecommerce.events.inventory.InventoryEvent;
import com.ecommerce.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryRemovalConsumer {
    private final InventoryService service;

    public InventoryRemovalConsumer(InventoryService service) {
        this.service = service;
    }

    @KafkaListener(topics = "inventory_removal_event")
    public void inventoryRemovalListener(InventoryEvent event){
        service.inventoryRemoval(event);
    }
}
