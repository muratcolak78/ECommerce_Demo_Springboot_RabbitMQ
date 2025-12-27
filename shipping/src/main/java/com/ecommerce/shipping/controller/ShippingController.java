package com.ecommerce.shipping.controller;

import com.ecommerce.shipping.service.ShippingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ecommerce/shipping")
public class ShippingController {
    private final ShippingService service;

    public ShippingController(ShippingService service) {
        this.service = service;
    }
}
