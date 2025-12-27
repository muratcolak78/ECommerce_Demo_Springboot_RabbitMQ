package com.ecommerce.inventory.service.impl;

import com.ecommerce.events.inventory.InventoryEvent;
import com.ecommerce.events.product.ProductEvent;
import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class InvetoryServiceImpl implements InventoryService {
    private final InventoryRepository repository;
    private final static Logger LOGGER= LoggerFactory.getLogger(InvetoryServiceImpl.class);

    public InvetoryServiceImpl(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addStock(ProductEvent event) {
        Inventory inventory=repository.findByProductId(event.getProductId())
                .orElseGet(Inventory::new);

        inventory.setProductId(event.getProductId());
        inventory.setStock(event.getStock());

        repository.save(inventory);
        LOGGER.info(String.format(">>> ProductEvent received from Kafka  product id: %s", event.getProductId()));

    }

    @Override
    public Map<String, Integer> getStocks() {

        Map<String, Integer> inventormap=new HashMap<String,Integer>();
        List<Inventory> inventoryList=repository.findAll();

        for(Inventory inventory:inventoryList){
            inventormap.put(String.valueOf(inventory.getProductId()), inventory.getStock());

        }
        return inventormap;
    }

    @Override
    public void inventoryReserved(InventoryEvent event) {
        Inventory inventory=repository.findByProductId(event.getProductId())
                .orElseThrow(() -> new IllegalStateException("Inventory not found"));

        int available= inventory.getStock()- inventory.getReserved();
        int qty= event.getAmount();

        if(qty<=0) return;
        if(qty>available){
            LOGGER.warn("Not enough stock for productId={}, available={}, requested={}",
                    event.getProductId(), available, qty);
            return;
        }


        inventory.setReserved(inventory.getReserved() + qty);

        repository.save(inventory);
        LOGGER.info(String.format(">>> ReserveEvent message received product id: %s", event.getProductId()));
    }

    @Override
    public void inventoryRemoval(InventoryEvent event) {
        Inventory inventory=repository.findByProductId(event.getProductId())
                .orElseThrow(() -> new IllegalStateException("Inventory not found"));


        int qty= event.getAmount();

        if(qty<=0) return;
        if(inventory.getReserved()<qty){
            LOGGER.warn("Cannot remove: reserved too low productId={}, reserved={}, requested={}",
                    event.getProductId(), inventory.getReserved(), qty);
            return;
        }
        inventory.setStock(inventory.getStock()-qty);
        inventory.setReserved(inventory.getReserved()-qty);

        repository.save(inventory);
        LOGGER.info(String.format(">>> RemoveEvent message received product id: %s", event.getProductId()));
    }
}
