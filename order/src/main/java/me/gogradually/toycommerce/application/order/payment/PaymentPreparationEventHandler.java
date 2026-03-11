package me.gogradually.toycommerce.application.order.payment;

import me.gogradually.toycommerce.application.order.event.OrderInfoCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentPreparationEventHandler {

    @EventListener
    public void handle(OrderInfoCompletedEvent event) {
        // Todo Placeholder hook for a later PG session creation/webhook integration.
    }
}
