package com.ecommerce.events.shipping;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingEvent {

    private Long orderId;

    private Long userId;

    private String fullName;

    private String street;

    private String zip;

    private String city;

    private String country;

    private String phone;

    private List<ShippingItemEvent> shippingItemEventList=new ArrayList<>();

}