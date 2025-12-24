package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.model.Product;
import com.ecommerce.catalog.model.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class CatalogController {

    private final WebClient webClient;

    @Value("${product.service.url}")
    private  String PRODUCT_URL ;
    @Value("${inventory.service.url}")
    private  String INVENTORY_URL;

    public CatalogController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping()
    public List<ProductDTO> getProductsWithStock(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {

        // 1) Product listesini çek
        List<Product> products = webClient.get()
                .uri(PRODUCT_URL)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {})
                .block();

        if (products == null || products.isEmpty()) {
            return List.of();
        }

        // 2) Inventory’den stok map’ini çek: {"1":100,"2":100,...}
        Map<String, Integer> stocks = webClient.get()
                .uri(INVENTORY_URL+"/getstocks")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Integer>>() {})
                .block();

        if (stocks == null) stocks = Map.of();

        // 3) Merge: productId -> stock
        Map<String, Integer> finalStocks = stocks;

        return products.stream().map(p -> {
            ProductDTO merged = new ProductDTO();
            merged.setId(p.getId());
            merged.setName(p.getName());
            merged.setBrand(p.getBrand());
            merged.setCategory(p.getCategory());
            merged.setDescription(p.getDescription());
            merged.setImageUrl(p.getImageUrl());
            merged.setPrice(p.getPrice());

            Integer stock = finalStocks.getOrDefault(String.valueOf(p.getId()), 0);
            merged.setStock(stock);

            return merged;
        }).toList();
    }
}
