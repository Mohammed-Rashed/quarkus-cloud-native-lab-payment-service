package com.mohammed.payment.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohammed.payment.messaging.event.StockReservedEvent;
import com.mohammed.payment.service.PaymentService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class InventoryEventConsumer {
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    public InventoryEventConsumer(
            ObjectMapper objectMapper,
            PaymentService paymentService
    ) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @Incoming("inventory-events-in")
    public void consume(String message) throws Exception {

        JsonNode json = objectMapper.readTree(message);

        String eventType = json.get("eventType").asText();

        if (!"STOCK_RESERVED".equals(eventType)) {
            return;
        }

        StockReservedEvent event = objectMapper.readValue(
                message,
                StockReservedEvent.class
        );

        System.out.println(
                "Payment received STOCK_RESERVED for order: "
                        + event.orderId()
        );

        boolean paymentSuccess =
                paymentService.processPayment(event.orderId());

        System.out.println(
                "Payment result: " + paymentSuccess
        );
    }
}
