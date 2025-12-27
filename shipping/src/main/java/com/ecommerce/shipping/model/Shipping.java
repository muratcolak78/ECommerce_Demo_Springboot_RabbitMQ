package com.ecommerce.shipping.model;

import com.ecommerce.shipping.model.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipping")
@Getter
@Setter
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id", nullable = false, unique=true)
    private Long orderId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String zip;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String country;
    @Column
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus shippingStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();
        this.updatedAt=this.createdAt;

        if (this.shippingStatus == null) {
            this.shippingStatus = ShippingStatus.READY;
        }

    }
    @PreUpdate
    public void preUpdate(){
        this.updatedAt=LocalDateTime.now();
    }

}
