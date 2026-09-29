package com.mohammed.payment.messaging;

import com.mohammed.payment.messaging.event.PaymentCompletedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import io.smallrye.reactive.messaging.kafka.Record;

@ApplicationScoped
public class PaymentEventProducer {
    @Channel("payment-events-out")
    Emitter<Record<String, Object>> emitter;
    public void sendPaymentCompleted(PaymentCompletedEvent event) {
        emitter.send(
                Record.of(
                        event.orderId().toString(),
                        event
                )
        );
    }

    public void sendPaymentFailed(PaymentFailedEvent event) {
        emitter.send(
                Record.of(
                        event.orderId().toString(),
                        event
                )
        );
    }

}
