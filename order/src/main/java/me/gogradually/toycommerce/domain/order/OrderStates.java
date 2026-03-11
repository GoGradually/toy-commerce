package me.gogradually.toycommerce.domain.order;

final class OrderStates {

    private static final OrderState CREATED = new CreatedOrderState();
    private static final OrderState INFO_COMPLETED = new InfoCompletedOrderState();
    private static final OrderState PAID = new PaidOrderState();
    private static final OrderState PAYMENT_FAILED = new PaymentFailedOrderState();
    private static final OrderState CANCELLED = new CancelledOrderState();

    private OrderStates() {
    }

    static OrderState from(OrderStatus status) {
        return switch (status) {
            case CREATED -> CREATED;
            case INFO_COMPLETED -> INFO_COMPLETED;
            case PAID -> PAID;
            case PAYMENT_FAILED -> PAYMENT_FAILED;
            case CANCELLED -> CANCELLED;
        };
    }
}
