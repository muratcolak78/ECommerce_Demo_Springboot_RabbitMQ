package com.ecommerce.product.service.imp;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.dto.ProductBasisDto;
import com.ecommerce.product.model.dto.ProductDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;


public interface ProductService {
    List<ProductDTO> getProductDTOS();
    List<Product> getProducts();
    BigDecimal getPriceById(Long id);
    String getNameById(Long id);
    List<ProductBasisDto> getNamesByIds(List<Long> ids);
    void addProduct(ProductDTO productDTO);
}
