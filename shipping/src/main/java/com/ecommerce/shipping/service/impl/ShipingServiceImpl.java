package com.ecommerce.shipping.service.impl;


import com.ecommerce.events.shipping.ShippingEvent;
import com.ecommerce.events.shipping.ShippingItemEvent;
import com.ecommerce.shipping.model.Shipping;
import com.ecommerce.shipping.model.ShippingItem;
import com.ecommerce.shipping.repository.ShippingItemRepository;
import com.ecommerce.shipping.repository.ShippingRepository;
import com.ecommerce.shipping.service.ShippingService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShipingServiceImpl implements ShippingService {
    private  final ShippingRepository repository;
    private final ShippingItemRepository itemRepository;
    private final static Logger LOGGER=LoggerFactory.getLogger(ShipingServiceImpl.class);

    public ShipingServiceImpl(ShippingRepository repository, ShippingItemRepository itemRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
    }
    @Transactional
    @Override
    public void getShippingEvent(ShippingEvent event) {
        LOGGER.info(String.format(">>> ShippingEvent message received orderId: %s", event.getOrderId()));

        Optional<Shipping> existing= repository.findByOrderId(event.getOrderId());
        if (existing.isPresent()) {
            LOGGER.info(">>> Shipping already exists for orderId={}", event.getOrderId());
            return;
        }

        Shipping shipping= new Shipping();
        shipping.setOrderId(event.getOrderId());
        shipping.setUserId(event.getUserId());
        shipping.setStreet(event.getStreet());
        shipping.setCity(event.getCity());
        shipping.setCountry(event.getCountry());
        shipping.setPhone(event.getPhone());
        shipping.setZip(event.getZip());
        shipping.setFullName(event.getFullName());

        try{
            Shipping savedShipping=repository.save(shipping);
            LOGGER.info(String.format(">>> Shipping saved orderId: %s", event.getOrderId()));
            Long shippingId= savedShipping.getId();

            List<ShippingItemEvent> shippingItemEventList=event.getShippingItemEventList();
            if(shippingItemEventList==null|| shippingItemEventList.isEmpty()){
                LOGGER.info("ShippingItemEventList {}", event.getOrderId());
                throw new IllegalStateException("ShippingItemEventList not found");

            }

            for(ShippingItemEvent itemEvent:shippingItemEventList){
                ShippingItem shippingItem=new ShippingItem();
                shippingItem.setShippingId(shippingId);
                shippingItem.setQuantity(itemEvent.getQuantity());
                shippingItem.setProductId(itemEvent.getProductId());
                shippingItem.setProductName(itemEvent.getProductName());
                itemRepository.save(shippingItem);
            }
            LOGGER.info(String.format(">>> ShippingItems saved orderId: %s", event.getOrderId()));
        }catch (DataIntegrityViolationException e){
            LOGGER.info("Shipping and sipping items  saved, {}",e.getMessage());
        }

    }
}
