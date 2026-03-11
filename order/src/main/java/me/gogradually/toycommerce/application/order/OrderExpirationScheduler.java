package me.gogradually.toycommerce.application.order;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderExpirationScheduler {

    private final OrderExpirationService orderExpirationService;

    @Scheduled(fixedDelayString = "${app.order.expiration-check-delay-ms:300000}")
    public void cancelExpiredOrders() {
        orderExpirationService.cancelExpiredOrders();
    }
}
