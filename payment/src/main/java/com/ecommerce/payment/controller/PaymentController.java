package com.ecommerce.payment.controller;


import com.ecommerce.payment.model.dto.PaymentRequest;
import com.ecommerce.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ecommerce/payment")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(
            @RequestBody PaymentRequest paymentrequest,
            Authentication authentication,
            @RequestHeader("Authorization") String header
            ){

        service.start(paymentrequest, header);
        return ResponseEntity.ok().build();
    }
}
