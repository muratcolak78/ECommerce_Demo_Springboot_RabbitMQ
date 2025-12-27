package com.ecommerce.inventory.service;


import com.ecommerce.events.inventory.InventoryEvent;
import com.ecommerce.events.product.ProductEvent;

import java.util.Map;

public interface InventoryService {
    void addStock(ProductEvent event);

    Map<String, Integer> getStocks();

    void inventoryReserved(InventoryEvent event);

    void inventoryRemoval(InventoryEvent event);
}
