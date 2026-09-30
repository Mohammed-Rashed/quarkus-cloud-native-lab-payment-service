package com.mohammed.payment.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentService {

    public boolean processPayment(Long orderId, int quantity) {

        System.out.println(
                "Processing payment for order: " + orderId
        );

        return quantity <= 3;
    }

}
