package com.ecommerce.order.service;


import com.ecommerce.events.payment.PaymentEvent;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.dto.CheckoutRequest;
import com.ecommerce.order.model.dto.OrderResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    Long checkOut(Long userId, String header, CheckoutRequest request);

    List<OrderItem> findByUserId(Long orderId, Long userId);

    List<OrderResponseDto> getOrders(Long userId);

    void updateStatus(PaymentEvent paymentEvent);

    BigDecimal getAmount(Long userId, Long orderId);
}
