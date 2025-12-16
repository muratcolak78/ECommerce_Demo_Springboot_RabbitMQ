package com.ecommerce.product.service.imp;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.dto.ProductDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface ProductService {
    List<ProductDTO> getProductDTOS();
    List<Product> getProducts();
}
