package com.ecommerce.order.model;

import com.ecommerce.order.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column
    private BigDecimal totalAmount;

    @Column(name = "shipping_name")
    private String shippingName;
    @Column(name = "shipping_street", nullable = false)
    private String shippingStreet;

    @Pattern(regexp = "\\d{5}")
    @Column(name = "shipping_zip", nullable = false)
    private String shippingZip;

    @Column(name = "shipping_city", nullable = false)
    private String shippingCity;
    @Column(name = "shipping_country", nullable = false)
    private String shippingCountry;
    @Column(name="shipping_phone")
    private String shippingPhone;
    @Column
    private String email;


    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime updatedAt;


    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();
        this.updatedAt=LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate(){
        this.updatedAt=LocalDateTime.now();
    }
}
