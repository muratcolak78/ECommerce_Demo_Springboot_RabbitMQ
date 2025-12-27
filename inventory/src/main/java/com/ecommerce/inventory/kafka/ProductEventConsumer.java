package com.ecommerce.inventory.kafka;


import com.ecommerce.events.product.ProductEvent;
import com.ecommerce.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventConsumer {
    private  final InventoryService service;

    public ProductEventConsumer(InventoryService service) {
        this.service = service;
    }

    @KafkaListener(topics = "product_added_event")
    public void productEventListener(ProductEvent event){
        service.addStock(event);
    }

}
