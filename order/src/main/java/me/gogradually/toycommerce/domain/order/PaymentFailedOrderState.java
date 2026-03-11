package me.gogradually.toycommerce.domain.order;

final class PaymentFailedOrderState implements OrderState {

    @Override
    public OrderStatus status() {
        return OrderStatus.PAYMENT_FAILED;
    }
}
