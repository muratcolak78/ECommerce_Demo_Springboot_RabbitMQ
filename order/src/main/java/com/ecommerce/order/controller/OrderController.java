package com.ecommerce.order.controller;

import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.dto.OrderResponseDto;
import com.ecommerce.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ecommerce/order")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Long>> checkOut(
            Authentication authentication,
            @RequestHeader("Authorization") String header                ){
        Long userId=Long.valueOf(authentication.getName());
        Long orderId=service.checkOut(userId, header);
        return  ResponseEntity.status(200).body(Map.of("orderId",orderId));

    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity< List<OrderItem>> getOrder(@PathVariable("orderId") Long orderId, Authentication authentication){
        Long userId=Long.valueOf(authentication.getName());
        List<OrderItem> orderItemList= service.findByUserId(orderId, userId);
        return ResponseEntity.ok(orderItemList);

    }
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders(Authentication authentication){
        Long userId=Long.valueOf(authentication.getName());
        List<OrderResponseDto> orderlist= service.getOrders(userId);
        return ResponseEntity.ok(orderlist);
    }

    ///  this endpoint work only, if payment service call for a totalAomunt of any order
    @GetMapping("/getamount")
    public BigDecimal getAmount(Authentication authentication,
                                @RequestParam Long orderId) {
        Long userId = Long.valueOf(authentication.getName());
        BigDecimal totalAmount = service.getAmount(userId, orderId);
        return totalAmount;
    }
}
