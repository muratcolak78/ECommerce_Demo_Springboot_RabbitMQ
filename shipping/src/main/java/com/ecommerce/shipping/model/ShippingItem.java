package com.ecommerce.shipping.model;


import com.ecommerce.shipping.model.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shipping_item")
@Getter
@Setter
public class ShippingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name ="shipping_id", nullable = false )
    private Long shippingId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();
        this.updatedAt=this.createdAt;
    }
    @PreUpdate
    public void preUpdate(){
        this.updatedAt=LocalDateTime.now();
    }

}
