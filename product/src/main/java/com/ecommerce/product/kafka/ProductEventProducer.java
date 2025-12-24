package com.ecommerce.product.kafka;

import com.ecommerce.product.model.eventmodel.ProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductEventProducer {
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;


    public ProductEventProducer(KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void productEventSend(ProductEvent event){
        kafkaTemplate.send("product_added_event", event.getProductId().toString(), event);
    }
}
