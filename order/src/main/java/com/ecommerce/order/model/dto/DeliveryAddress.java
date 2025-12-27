package com.ecommerce.order.model.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryAddress {
    private String fullName;
    private String street;
    private String zip;
    private String city;
    private String country;
    private String phone;
    private String email;
}
