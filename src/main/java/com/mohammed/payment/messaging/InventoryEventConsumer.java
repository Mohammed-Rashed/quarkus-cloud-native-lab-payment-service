package com.mohammed.payment.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohammed.payment.messaging.event.PaymentCompletedEvent;
import com.mohammed.payment.messaging.event.StockReservedEvent;
import com.mohammed.payment.service.PaymentService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class InventoryEventConsumer {
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final PaymentEventProducer paymentEventProducer;
    public InventoryEventConsumer(
            ObjectMapper objectMapper,
            PaymentService paymentService,
            PaymentEventProducer paymentEventProducer
    ) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
        this.paymentEventProducer = paymentEventProducer;
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
                paymentService.processPayment(event.orderId(),event.quantity());

        System.out.println(
                "Payment result: " + paymentSuccess
        );
        if (paymentSuccess) {

            PaymentCompletedEvent paymentCompletedEvent =
                    new PaymentCompletedEvent(
                            UUID.randomUUID(),
                            "PAYMENT_COMPLETED",
                            Instant.now(),
                            1,
                            event.orderId()
                    );

            paymentEventProducer.sendPaymentCompleted(
                    paymentCompletedEvent
            );
            System.out.println(
                    "sendPaymentCompleted: "+ paymentCompletedEvent.orderId()
            );
        }else {

            PaymentFailedEvent paymentFailedEvent =
                    new PaymentFailedEvent(
                            UUID.randomUUID(),
                            "PAYMENT_FAILED",
                            Instant.now(),
                            1,
                            event.orderId(),
                            event.productId(),
                            event.quantity(),
                            "PAYMENT_DECLINED"
                    );

            paymentEventProducer.sendPaymentFailed(
                    paymentFailedEvent
            );
        }
    }
}
