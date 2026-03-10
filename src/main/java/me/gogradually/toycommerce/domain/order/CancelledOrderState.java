package me.gogradually.toycommerce.domain.order;

final class CancelledOrderState implements OrderState {

    @Override
    public OrderStatus status() {
        return OrderStatus.CANCELLED;
    }
}
