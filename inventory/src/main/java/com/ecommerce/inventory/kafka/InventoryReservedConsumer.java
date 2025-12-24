package com.ecommerce.inventory.kafka;

import com.ecommerce.inventory.model.eventmodel.InventoryEvent;
import com.ecommerce.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservedConsumer {
    private final InventoryService service;

    public InventoryReservedConsumer(InventoryService service) {
        this.service = service;
    }

    @KafkaListener(topics = "inventory_reserved_event", groupId = "mygroup")
    public void inventoryReservedListener(InventoryEvent event){
              service.inventoryReserved(event);
    }
}
