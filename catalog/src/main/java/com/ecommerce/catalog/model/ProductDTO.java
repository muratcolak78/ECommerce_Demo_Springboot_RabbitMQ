package com.ecommerce.catalog.model;

import com.ecommerce.catalog.model.Category;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO extends Product{
    private Integer stock;
  }
