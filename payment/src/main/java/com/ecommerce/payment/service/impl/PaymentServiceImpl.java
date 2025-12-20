package com.ecommerce.payment.service.impl;


import com.ecommerce.payment.kafka.PaymentEventProducer;
import com.ecommerce.payment.model.Payment;
import com.ecommerce.payment.model.dto.PaymentEvent;
import com.ecommerce.payment.model.dto.PaymentRequest;
import com.ecommerce.payment.model.enums.EventStatus;
import com.ecommerce.payment.model.enums.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final WebClient webClient;
    private final PaymentEventProducer producer;
    private final static Logger LOGGER= LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${order.service.url}")
    private String ORDER_SERVICE_URL;

    public PaymentServiceImpl(PaymentRepository repository, WebClient webClient, PaymentEventProducer producer) {
        this.repository = repository;
        this.webClient = webClient;
        this.producer = producer;
    }

    @Override
    @Transactional
    public void start(PaymentRequest paymentrequest, String header) {
        ///  take amount from order service
        BigDecimal totalAmount=getTotalAmountFromOrderService(paymentrequest.getOrderId(), header);

        /// create a new Payment to save
        Payment payment=new Payment();
        payment.setOrderId(paymentrequest.getOrderId());
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.STARTED);
        payment.setMethod(paymentrequest.getPaymentMethode());

        ///  save payment
        Payment savedPayment= repository.save(payment);

        ///  wait 5 seconds for simulation
        boolean success = simutationWait();

        ///  if payment succeeded update saved payment
        savedPayment.setStatus(success ? PaymentStatus.SUCCEEDED:PaymentStatus.FAILED);
        repository.save(savedPayment);

        ///  then send a payment event to kafka
        PaymentEvent event=new PaymentEvent();
        event.setOrderId(savedPayment.getOrderId());
        event.setStatus(success? EventStatus.PAID: EventStatus.FAILED);
        producer.paymentEventSend(event);
        LOGGER.info(String.format("Payment event sendet to kafka -> %s", event));


    }


    public boolean simutationWait(){
        try{
            Thread.sleep(1000);
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    private BigDecimal getTotalAmountFromOrderService(Long orderId, String header) {
        String fullUrl = ORDER_SERVICE_URL + "/getamount?orderId=" + orderId;
        return webClient.get()
                .uri(fullUrl)
                .header("Authorization", header)
                .retrieve()
                .bodyToMono(BigDecimal.class)
                .block();
    }


}
