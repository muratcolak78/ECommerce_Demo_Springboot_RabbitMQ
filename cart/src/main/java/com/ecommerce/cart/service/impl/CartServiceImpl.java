package com.ecommerce.cart.service.impl;

import com.ecommerce.cart.model.*;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.cart.service.CartService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl  implements CartService {

    private final WebClient webClient;
    private final CartRepository repository;
    private final static Logger LOGGER= LoggerFactory.getLogger(CartServiceImpl.class);

    @Value("${product.service.url}")
    private String PRODUCT_URL;

    public CartServiceImpl(WebClient webClient, CartRepository repository) {
        this.webClient = webClient;
        this.repository = repository;
    }

    // add item ot database with user id
    @Override
    public void addToCart(Long userId, AddToCartRequest addToCartRequest) {
        Optional<CartItem> isThereSameCart=repository.findByUserIdAndProductId(userId, addToCartRequest.getProductId());
        if(isThereSameCart.isPresent()){
            CartItem cartItem= isThereSameCart.get();
            cartItem.setQuantity(cartItem.getQuantity()+ addToCartRequest.getQuantity());
            repository.save(cartItem);
            LOGGER.info(String.format("Product added to cart productId: ->%s", cartItem.getProductId()));
        }else{
            Long productId= addToCartRequest.getProductId();
            BigDecimal price=getPriceById(productId);
            CartItem item=new CartItem();
            item.setUserId(userId);
            item.setQuantity(addToCartRequest.getQuantity());
            item.setProductId(productId);
            item.setPriceSnapshot(price);
            repository.save(item);
            LOGGER.info(String.format("Product added to cart productId: ->%s", item.getProductId()));
        }

   }

   // get all cart items
    @Override
    public List<CartItemDto> getItems(Long userId) {
        // get all cart items frm database
        List<CartItem> itemList=repository.findByUserId(userId);

        if (itemList.isEmpty()) {
            return List.of();
        }

        // get all productId from item list
        List<Long> itemsIds=itemList.stream()
                .map(CartItem::getProductId)
                .toList();
        // sent all items productIds to pruductServise, beacuse we need productName list
        List<ProductBasisDto> productBasisDtoList=getAllproductsNameById(itemsIds);

        // make productBasicDto a map, so we can take every name of product
        Map<Long, String> productNameMap =
                productBasisDtoList.stream()
                        .collect(Collectors.toMap(
                                ProductBasisDto::getProductId,
                                ProductBasisDto::getProductname
                        ));

        return itemList.stream().map(cartItem -> {

                    CartItemDto itemDto=new CartItemDto();

                    itemDto.setProductId(cartItem.getProductId());
                    itemDto.setQuantity(cartItem.getQuantity());
                    itemDto.setProductName(
                            productNameMap.getOrDefault(
                                    cartItem.getProductId(),
                                    "Unknown"
                            )
                    );
                    itemDto.setPriceSnapshot(cartItem.getPriceSnapshot());

                     return itemDto;

        }).toList();

    }
    // delete cart by userid and productid
    @Modifying
    @Transactional
    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        CartItem cartItem=repository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(()-> new RuntimeException("Cart item not found"));
        repository.delete(cartItem);
        LOGGER.info(String.format("Product removed from cart productId: ->%s", productId));
    }

    // delete all cart items by user id
    @Transactional
    @Override
    public void cartClear(Long userId) {
        repository.deleteByUserId(userId);
        LOGGER.info(String.format("Cart cleared userID: ->%s", userId));
    }

    // update quantity
    @Override
    public void updateQuantity(Long userId, Long productId, UpdateAction action) {
        CartItem cartItem=repository.findByUserIdAndProductId(userId,productId)
                .orElseThrow(()-> new RuntimeException("Cart item not found"));

        if(action== UpdateAction.INCREMENT){
            cartItem.setQuantity(cartItem.getQuantity()+1);
            repository.save(cartItem);
            return;
        }

        if(action== UpdateAction.DECREMENT){
            if(cartItem.getQuantity()<=1){
                repository.delete(cartItem);
                return;
            }
            cartItem.setQuantity(cartItem.getQuantity()-1);
            repository.save(cartItem);
            return;
        }

        throw new IllegalArgumentException("Invalid action: " + action);
    }

    // get productsprice by id  with weblient from product service
    private BigDecimal getPriceById(Long id){
        return webClient.get()
                .uri(PRODUCT_URL+"/getPrice/"+id)
                .retrieve()
                .bodyToMono(BigDecimal.class)
                .block();
    }

    // get all productsname by ids
    private List<ProductBasisDto> getAllproductsNameById(List<Long> ids){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8081)
                        .path("/api/ecommerce/product/getName")
                        .queryParam("ids", ids)
                        .build())
                .retrieve()
                .bodyToFlux(ProductBasisDto.class)
                .collectList()
                .block();
    }


}
