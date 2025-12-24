package com.ecommerce.product.controller;


import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.dto.ProductBasisDto;
import com.ecommerce.product.model.dto.ProductDTO;
import com.ecommerce.product.service.imp.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/ecommerce/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/getPrice/{id}")
    public BigDecimal getPriceById(@PathVariable("id") Long id){
        return productService.getPriceById(id);
    }
    @GetMapping("/getName/{id}")
    public String getNameById(@PathVariable("id") Long id){
        return productService.getNameById(id);

    }

    @GetMapping("/getName")
    public List<ProductBasisDto> getNames(@RequestParam List<Long> ids) {
        return productService.getNamesByIds(ids);
    }

    @PostMapping("/addproduct")
    public ResponseEntity<Void> addProduct(@RequestBody ProductDTO productDTO){
        productService.addProduct(productDTO);
        return ResponseEntity.ok().build();
    }
}
