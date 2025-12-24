package com.ecommerce.inventory.service;


import com.ecommerce.inventory.model.eventmodel.InventoryEvent;
import com.ecommerce.inventory.model.eventmodel.ProductEvent;

import java.util.Map;

public interface InventoryService {
    void addStock(ProductEvent event);

    Map<String, Integer> getStocks();

    void inventoryReserved(InventoryEvent event);

    void inventoryRemoval(InventoryEvent event);
}
