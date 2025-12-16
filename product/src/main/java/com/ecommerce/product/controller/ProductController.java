package com.ecommerce.product.controller;


import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.dto.ProductDTO;
import com.ecommerce.product.service.imp.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/product")
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/productdtos")
    public ResponseEntity<List<ProductDTO>> getProductDTOS() {
        return ResponseEntity.ok(productService.getProductDTOS());
    }
    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }
}
