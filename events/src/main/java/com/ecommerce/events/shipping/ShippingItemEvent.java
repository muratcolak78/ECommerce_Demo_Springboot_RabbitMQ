package com.ecommerce.events.shipping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingItemEvent {

     private Long productId;

    private String productName;

    private Integer quantity;


}
