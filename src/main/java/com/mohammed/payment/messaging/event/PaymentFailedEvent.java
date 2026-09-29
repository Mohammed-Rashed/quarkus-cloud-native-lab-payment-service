package com.mohammed.payment.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        int version,
        Long orderId,
        Long productId,
        int quantity,
        String reason
) {
}
