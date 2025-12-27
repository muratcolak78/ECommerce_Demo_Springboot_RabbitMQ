package com.ecommerce.order.service.impl;

import com.ecommerce.events.inventory.InventoryEvent;
import com.ecommerce.events.payment.EventStatus;
import com.ecommerce.events.payment.PaymentEvent;
import com.ecommerce.events.shipping.ShippingEvent;
import com.ecommerce.events.shipping.ShippingItemEvent;
import com.ecommerce.order.kafka.InventoryRemovalProducer;
import com.ecommerce.order.kafka.InventoryReservedProducer;
import com.ecommerce.order.kafka.ShippingEventProducer;
import com.ecommerce.order.model.*;
import com.ecommerce.order.model.dto.*;
import com.ecommerce.order.model.enums.Status;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.repository.OrderRepository;
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
    private final InventoryReservedProducer inventoryreservedProducer;
    private final InventoryRemovalProducer inventoryRemovalProducer;
    private final ShippingEventProducer shippingEventProducer;
    private final static Logger LOGGER= LoggerFactory.getLogger(OrderServiceImpl.class);

    @Value("${cart.service.url}")
    private String CART_SERVICE_URL;

    public OrderServiceImpl(OrderRepository repository, OrderItemRepository itemRepository, WebClient webClient, InventoryReservedProducer inventoryreservedProducer, InventoryRemovalProducer inventoryRemovalProducer, ShippingEventProducer shippingEventProducer) {
        this.orderRepository = repository;
        this.itemRepository = itemRepository;
        this.webClient = webClient;
        this.inventoryreservedProducer = inventoryreservedProducer;
        this.inventoryRemovalProducer = inventoryRemovalProducer;
        this.shippingEventProducer = shippingEventProducer;
    }


    @Override
    @Transactional
    public Long checkOut(Long userId, String header, CheckoutRequest request) {
        List<CartItemDto> items=getUsersCartItems(header);
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        if (request.getDeliveryAddress() == null) {
            throw new IllegalStateException("Delivery address is required");
        }
        if (request.getDeliveryAddress().getEmail() == null || request.getDeliveryAddress().getEmail().isBlank()) {
            throw new IllegalStateException("Email is required");
        }

        /// Create Order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(Status.CREATED);
        order.setTotalAmount(getTotalAmount(items));
        ///get and set address
        DeliveryAddress address= request.getDeliveryAddress();
        order.setShippingName(address.getFullName());
        order.setShippingStreet(address.getStreet());
        order.setShippingCity(address.getCity());
        order.setShippingZip(address.getZip());
        order.setShippingCountry(address.getCountry());
        order.setShippingPhone(address.getPhone());
        order.setEmail(address.getEmail());

        ///  save Order to database
        Order savedOrder = orderRepository.save(order);
        LOGGER.info(String.format("order saved status: -> %s%s",order.getId(), order.getStatus()));

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
        LOGGER.info(String.format(">>> Order items saved -> %s",savedOrder));
             /// Clear Cart
        clearCart(header);

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
        LOGGER.info(String.format(">>> PaymentEvent Message received from kafka topic ->%s", paymentEvent.getOrderId()));
        Order order=orderRepository.findById(paymentEvent.getOrderId())
                .orElseThrow(()->new IllegalStateException("Order not found"));

        if (order.getStatus()!=Status.CREATED) return;

        if(paymentEvent.getStatus()== EventStatus.PAID){
            order.setStatus(Status.PAID);
            LOGGER.info(String.format(">>> Order updated status:-> %s%s",order.getId(), order.getStatus()));

            sendEventToInventoryToRemoveQuantity(order);

            LOGGER.info(String.format(">>> InventoryEvent is sent to Kafka -> %s ",order.getUserId()));

            sendEventToShippingToSaveOrderData(order);
            LOGGER.info(String.format(">>> ShippingEvent is sent to Kafka -> %s ",order.getUserId()));

        }else if(paymentEvent.getStatus()==EventStatus.FAILED){
            order.setStatus(Status.FAILED);
        }



        orderRepository.save(order);



    }

    private void sendEventToShippingToSaveOrderData(Order order) {
            List<OrderItem> orderItemList=itemRepository.findByOrderId(order.getId());

            if(orderItemList.isEmpty()) throw  new IllegalStateException("OrderItems not found");

            ShippingEvent shippingEvent=new ShippingEvent();
            shippingEvent.setOrderId(order.getId());
            shippingEvent.setUserId(order.getUserId());
            shippingEvent.setFullName(order.getShippingName());
            shippingEvent.setStreet(order.getShippingStreet());
            shippingEvent.setCity(order.getShippingCity());
            shippingEvent.setZip(order.getShippingZip());
            shippingEvent.setCountry(order.getShippingCountry());
            shippingEvent.setPhone(order.getShippingPhone()==null?"00000": order.getShippingPhone());

            for(OrderItem item:orderItemList){
                ShippingItemEvent shippingItemEvent=new ShippingItemEvent();
                shippingItemEvent.setProductId(item.getProductId());
                shippingItemEvent.setProductName(item.getProductName());
                shippingItemEvent.setQuantity(item.getQuantity());
                shippingEvent.getShippingItemEventList().add(shippingItemEvent);

            }

            shippingEventProducer.sendShippingEvent(shippingEvent);


    }


    private void sendEventToInventoryToRemoveQuantity(Order order) {

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
