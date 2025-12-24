package com.ecommerce.order.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic inventoryResevedEvent(){
        return TopicBuilder
                .name("inventory_reserved_event")
                .build();
    }
    @Bean
    public NewTopic inventoryRemovalEvent(){
        return TopicBuilder
                .name("inventory_removal_event")
                .build();
    }

}
