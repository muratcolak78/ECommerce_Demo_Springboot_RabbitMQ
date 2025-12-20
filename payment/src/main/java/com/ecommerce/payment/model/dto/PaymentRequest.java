package com.ecommerce.payment.model.dto;

import com.ecommerce.payment.model.enums.PaymentMethode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long orderId;
    private PaymentMethode paymentMethode;
}
