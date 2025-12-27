package com.ecommerce.shipping.service;


import com.ecommerce.events.shipping.ShippingEvent;

public interface ShippingService {
    void getShippingEvent(ShippingEvent event);
}
