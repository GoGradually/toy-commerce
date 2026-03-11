package me.gogradually.toycommerce.domain.order;

final class PaidOrderState implements OrderState {

    @Override
    public OrderStatus status() {
        return OrderStatus.PAID;
    }

    @Override
    public boolean markPaid(Order order) {
        return false;
    }
}
