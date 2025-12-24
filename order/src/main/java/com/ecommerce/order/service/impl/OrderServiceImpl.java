package com.ecommerce.order.service.impl;

import com.ecommerce.order.kafka.InventoryRemovalProducer;
import com.ecommerce.order.kafka.InventoryReservedProducer;
import com.ecommerce.order.model.*;
import com.ecommerce.order.model.dto.CartItemDto;
import com.ecommerce.order.model.dto.OrderItemResponseDto;
import com.ecommerce.order.model.dto.OrderResponseDto;
import com.ecommerce.order.model.enums.EventStatus;
import com.ecommerce.order.model.enums.Status;
import com.ecommerce.order.model.event.InventoryEvent;
import com.ecommerce.order.model.event.PaymentEvent;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.kafka.OrderEventProducer;
import com.ecommerce.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final WebClient webClient;
    private final OrderEventProducer producer;
    private final InventoryReservedProducer inventoryreservedProducer;
    private final InventoryRemovalProducer inventoryRemovalProducer;
    private final static Logger LOGGER= LoggerFactory.getLogger(OrderServiceImpl.class);

    @Value("${cart.service.url}")
    private String CART_SERVICE_URL;

    public OrderServiceImpl(OrderRepository repository, OrderItemRepository itemRepository, WebClient webClient, OrderEventProducer producer, InventoryReservedProducer inventoryreservedProducer, InventoryRemovalProducer inventoryRemovalProducer) {
        this.orderRepository = repository;
        this.itemRepository = itemRepository;
        this.webClient = webClient;
        this.producer = producer;
        this.inventoryreservedProducer = inventoryreservedProducer;
        this.inventoryRemovalProducer = inventoryRemovalProducer;
    }


    @Override
    @Transactional
    public Long checkOut(Long userId, String header) {
        List<CartItemDto> items=getUsersCartItems(header);
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        /// Create Order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(Status.CREATED);
        order.setTotalAmount(getTotalAmount(items));

        ///  save Order to database
        Order savedOrder = orderRepository.save(order);

        /// Save Order Items
        for (CartItemDto cart : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(cart.getProductId());
            orderItem.setProductName(cart.getProductName());
            orderItem.setUnitPrice(cart.getPriceSnapshot());
            orderItem.setQuantity(cart.getQuantity());

            /// create inventory reserved event
            InventoryEvent event=new InventoryEvent();
            event.setProductId(orderItem.getProductId());
            event.setAmount(orderItem.getQuantity());
            ///  sent evenet to kafka topic
            inventoryreservedProducer.sendInventoryEvent(event);

            itemRepository.save(orderItem);
        }
        /// create order_event to send kafka order_saved topic
        OrderCreatedEvent event=new OrderCreatedEvent();
        event.setOrderId(savedOrder.getId());
        event.setUserId(savedOrder.getUserId());
        event.setStatus(savedOrder.getStatus());
        event.setTotalAmount(savedOrder.getTotalAmount());

        /// send event
       /// producer.publishOrderSaved(event);

        /// Clear Cart
        clearCart(header);
        LOGGER.info(String.format("order saved -> %s",order));
        return savedOrder.getId();

    }

    @Override
    public List<OrderItem> findByUserId(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        List<OrderItem> orderItemList=itemRepository.findByOrderId(order.getId());

        if(orderItemList.isEmpty()){
            throw new IllegalStateException("OrderItems is empty");
        }
        return orderItemList;
    }

    @Override
    public List<OrderResponseDto> getOrders(Long userId) {

        List<Order> orderList=orderRepository.findAllByUserId(userId);

        if(orderList.isEmpty()) return List.of();

        List<OrderResponseDto> dtoList=new ArrayList<>();

        for(Order order:orderList){
            OrderResponseDto dto=new OrderResponseDto();
            dto.setOrderId(order.getId());
            dto.setStatus(order.getStatus());
            dto.setTotalAmount(order.getTotalAmount());

            List<OrderItem> orderItemList=itemRepository.findByOrderId(order.getId());

            dto.setItems(setListFromOrderItems(orderItemList));

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    @Transactional
    public void updateStatus(PaymentEvent paymentEvent) {
        LOGGER.info(String.format("Message received from kafka topic ->%s", paymentEvent));
        Order order=orderRepository.findById(paymentEvent.getOrderId())
                .orElseThrow(()->new IllegalStateException("Order not found"));

        if (order.getStatus()!=Status.CREATED) return;

        LOGGER.info(String.format("order status -> %s %s",order.getUserId(), order.getStatus().name()));
        if(paymentEvent.getStatus()==EventStatus.PAID){
            order.setStatus(Status.PAID);
        }else if(paymentEvent.getStatus()==EventStatus.FAILED){
            order.setStatus(Status.FAILED);
        }



        orderRepository.save(order);
        setInventoryRemoval(order);
        LOGGER.info(String.format("order status updated -> %s %s",order.getUserId(),order.getStatus().name()));


    }

    private void setInventoryRemoval(Order order) {

        if(order.getStatus()!=Status.PAID) return;

        List<OrderItem> orderItemList=itemRepository.findByOrderId(order.getId());
        if(orderItemList.isEmpty()){
            throw new IllegalStateException("OrderItems not found");
        }


        for (OrderItem orderItem : orderItemList) {
            ///  create event
            InventoryEvent event=new InventoryEvent();
            event.setProductId(orderItem.getProductId());
            event.setAmount(orderItem.getQuantity());
            ///  send to kafka topic
            inventoryRemovalProducer.sendInventoryRemovalEvent(event);

        }
    }

    @Override
    public BigDecimal getAmount(Long userId, Long orderId) {
        Order order=orderRepository.findByIdAndUserId(orderId,userId)
                .orElseThrow(()->new IllegalStateException("Order not found"));

        return order.getTotalAmount();
    }

    private void clearCart(String authHeader) {
        webClient.delete()
                .uri(CART_SERVICE_URL+"/clear")
                .header("Authorization", authHeader)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
    private List<CartItemDto> getUsersCartItems(String header){
        return webClient.get()
                .uri(CART_SERVICE_URL+"/items")
                .header("Authorization", header)
                .retrieve()
                .bodyToFlux(CartItemDto.class)
                .collectList()
                .block();
    }


    private BigDecimal getTotalAmount(List<CartItemDto> cartList) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemDto cart : cartList) {
            BigDecimal lineTotal = cart.getPriceSnapshot()
                    .multiply(BigDecimal.valueOf(cart.getQuantity()));
            total = total.add(lineTotal);
        }
        return total;
    }
    private List<OrderItemResponseDto> setListFromOrderItems(List<OrderItem> orderItemList){
        List<OrderItemResponseDto> orderResponseDtoList=new ArrayList<>();
        for(OrderItem orderItem:orderItemList){
            OrderItemResponseDto orderResponseDto=new OrderItemResponseDto();

            orderResponseDto.setProductId(orderItem.getProductId());
            orderResponseDto.setProductName(orderItem.getProductName());
            orderResponseDto.setPriceSnapshot(orderItem.getUnitPrice());
            orderResponseDto.setQuantity(orderItem.getQuantity());
            orderResponseDto.setLineTotal(orderItem.getLineTotal());

            orderResponseDtoList.add(orderResponseDto);

        }
        return orderResponseDtoList;
    }
}
