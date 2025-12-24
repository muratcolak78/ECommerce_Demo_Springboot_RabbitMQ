package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ecommerce/inventory")
public class InventoryController {
    private final InventoryService service;


    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/getstocks")
    public Map<String, Integer> getStocks(){
        return service.getStocks();
    }
}
