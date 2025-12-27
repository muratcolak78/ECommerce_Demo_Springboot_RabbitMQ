package com.ecommerce.shipping.repository;


import com.ecommerce.shipping.model.ShippingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingItemRepository extends JpaRepository<ShippingItem, Long> {
}
