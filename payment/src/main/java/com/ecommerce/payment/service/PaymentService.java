package com.ecommerce.payment.service;


import com.ecommerce.payment.model.dto.PaymentRequest;

public interface PaymentService {
    void start(PaymentRequest paymentrequest, String header);
}
